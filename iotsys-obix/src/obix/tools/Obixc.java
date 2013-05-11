/*
 * This code licensed to public domain
 */
package obix.tools;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import obix.*;
import obix.io.ObixDecoder;
import obix.io.ObixEncoder;

/**
 * Obixc compiles obix prototypes into Java source files.
 * Each well known obj (has href) gets mapped into a interface
 * and a class.  The interface defines the sub-object accessor
 * methods.  The class implements the accessors and provides
 * a static init() to add defaults to an Obj's hash map.
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class Obixc   
{                 

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  public Obixc(File destDir, String packageName)
  {
    this.destDir = destDir;                    
    this.packageName = packageName;
  }

////////////////////////////////////////////////////////////////
// Compile
////////////////////////////////////////////////////////////////

  public void compile(InputStream in)
    throws Exception
  {           
    // decode document             
    ObixDecoder decoder = new ObixDecoder(in);
    decoder.setUseContracts(false);  
    Obj root = decoder.decodeDocument();
    
    // map well known objects to types
    map(root);                        
    
    // compile the well types to .java files
    for (int i=0; i<types.length; ++i)      
      writeInterface(types[i]);
    
    // write ContractInit.java
    writeContractInit();
  }

////////////////////////////////////////////////////////////////
// Map
////////////////////////////////////////////////////////////////

  public void map(Obj proto)
    throws Exception
  {                                                           
    // first recursively map the protos into Type instances
    ArrayList acc = new ArrayList();                  
    map(proto, acc);
    types = (Type[])acc.toArray(new Type[acc.size()]);     
    
    // map proto contracts into type references
    for (int i=0; i<types.length; ++i)
      mapContracts(types[i]);   
      
    // map proto children
    for (int i=0; i<types.length; ++i)
      mapChildren(types[i]);   
      
    // solve covariance issues
    for (int i=0; i<types.length; ++i)
      mapChildren(types[i]);   
  }
  
  private void map(Obj proto, ArrayList acc)
  {
    // if well known map to Java type    
    Uri href = proto.getHref();              
    if (href != null && !href.isFragment())
      acc.add(toType(proto));
    
    // recurse looking for other well known prototypes
    Obj[] kids = proto.list();
    for (int i=0; i<kids.length; ++i)
      map(kids[i], acc);
  }
  
  private Type toType(Obj proto)
  {                                       
    String name = proto.getName();
    if (name == null)
    {
      String href = proto.getHref().toString();
      int end = href.lastIndexOf('/');
      if (end < 0) end = href.lastIndexOf(':');
      name = href.substring(end+1);
    }
    
    Type type = new Type();
    type.name  = name;
    type.proto = proto;
    type.href  = proto.getHref();      
    
    return type;
  }               
  
  private void mapContracts(Type type)
  {
    ArrayList acc = new ArrayList();
    
    // always implement yourself
    acc.add(type);
    
    Contract c = type.proto.getIs();
    if (c == null) c = new Contract("");

    Uri[] list = c.list();
    for (int i=0; i<list.length; ++i)
    {
      Type t = hrefToType(list[i]);
      if (t != null) acc.add(t); 
    }                               
    
    type.contracts = (Type[])acc.toArray(new Type[acc.size()]);
  }                        

  private void mapChildren(Type type)
  {  
    ArrayList acc = new ArrayList();
    
    Obj[] kids = type.proto.list(); 
    for (int i=0; i<kids.length; ++i)
    {
      Obj kid = kids[i];
      if (kid.getName() == null) continue; 
      acc.add(mapChild(type, kids[i]));
    }
    
    type.children = (Child[])acc.toArray(new Child[acc.size()]);
  }

  private Child mapChild(Type type, Obj kid)
  {
    Child child = new Child();
    child.parent        = type;
    child.name          = kid.getName();
    child.obj           = kid;
    child.className     = toClassName(kid, false);  
    child.implClassName = toClassName(kid, true);  
    
    // check for covariance, we start off assuming that
    // the getter will use the same Java class as the child
    // object; however oBIX might covariantly narrow the
    // child in sub-contracts.  Java 1.5 will allow this, but
    // for now let's make this code work with 1.4 
    child.retClassName = child.className;
    
    return child;
  }
  
  private Type hrefToType(Uri href)
  {
    for (int i=0; i<types.length; ++i)
      if (types[i].href.equals(href))
        return types[i];
    return null;
  }

////////////////////////////////////////////////////////////////
// Write Java
////////////////////////////////////////////////////////////////
  
  /**
   * Write the Java source for an interface to model
   * the specified type.  The interface contains an accessor
   * method for each child object.  If the child object
   * has meta-data like facets or it's own contract then
   * we also generate a "contract field" with the meta-data
   * defined a string contract with the actual XML.
   */
  public void writeInterface(Type type)
    throws Exception
  {                         
    String name = type.name;
    
    openFile(name);
    
    // write source
    wheader("interface", name);
    w("  extends IObj");   
    for (int i=1; i<type.contracts.length; ++i)  
      w(", ").w(type.contracts[i].name);
    w("\n");
    w("{\n").flush();  
    
    // write sub-object accessor methods
    for(int i=0; i<type.children.length; ++i)
    {
      Child kid = type.children[i];
      w("\n");                   
      if (needsContractField(kid.obj))
        w("  public static final String ").w(kid.name).w("Contract = \"").w(toContract(kid.obj)).w("\";\n");
      w("  public ").w(kid.className).w(" ").w(kid.name).w("();\n");
    }
    w("\n");
    w("}\n");        
    
    closeFile();
  }       
  
  /**
   * We only include contract fields for children which
   * aren't simple object types with just a name - examples 
   * include when the child object specifies facets, a default 
   * val attribute, or has it's own contract list.  The way 
   * we test is to compare the encoding for the actual object 
   * against what the default constructor does.  This matches
   * what ObixAssembler does when generating a constructor.
   */
  boolean needsContractField(Obj obj)
  {
    Obj def = Obj.toObj(obj.getElement());
    def.setName(obj.getName());                        
    
    String a = toContract(obj);                            
    String b = toContract(def);
    return !a.equals(b);
  }
  
  /**
   * Generate the XML for a contract field.
   */  
  String toContract(Obj obj)
  {       
    String xml = ObixEncoder.toString(obj);
    xml = xml.replace('"', '\'');
    xml = xml.replace('\n', ' ');
    xml = xml.replace('\r', ' ');
    return xml.trim();
  } 
  
////////////////////////////////////////////////////////////////
// Contract Init
////////////////////////////////////////////////////////////////

  public void writeContractInit()
    throws Exception
  {           
    String name = "ContractInit";
    
    openFile(name);
    
    // write source
    wheader("class", name);
    w("{\n");    
    w("\n");         

    w("  public static void init()\n");
    w("  {\n");                                       
    for (int i=0; i<types.length; ++i)
    {                           
      Type type = types[i];
      w("    ContractRegistry.put(\"").w(type.href.get()).w("\", \"").w(packageName).w(".").w(type.name).w("\");\n");
    }
    w("  }\n");
    w("\n");   
    w("}\n");   
    closeFile();
  }    

////////////////////////////////////////////////////////////////
// Utils
////////////////////////////////////////////////////////////////

  String toClassName(Obj obj, boolean impl)
  {                
    if (obj instanceof Ref) return "Ref";
    
    Contract is = obj.getIs();
    if (is != null && is.size() > 0)
    {
      Type use = hrefToType(is.primary());
      if (use != null) 
      {
        return use.name; 
      }
    }     
    
    return capitalize(obj.getElement());
  }

  String capitalize(String s)
  {
    return ""+Character.toUpperCase(s.charAt(0)) + s.substring(1);
  }

  Obixc w(String s)
  {
    out.print(s);
    return this;
  }

  Obixc wdate()
  {
    return w(new SimpleDateFormat("dd MMM yy").format(new Date()));
  }            
  
  void openFile(String name)
    throws Exception
  {
    File outFile = new File(destDir, name + ".java");
    System.out.println("Compile " + name + " -> " + outFile);
    out = new PrintWriter(new FileOutputStream(outFile));
  }
  
  void closeFile()
    throws Exception
  {
    out.flush();                                  
    out.close();     
  }

  void wheader(String classType, String name)
  {
    w("package ").w(packageName).w(";\n");
    w("\n");
    w("import obix.*;");
    w("\n");
    w("\n");
    w("/**\n");
    w(" * ").w(name).w("\n");
    w(" *\n");
    w(" * @author    obix.tools.Obixc\n");
    w(" * @creation  ").wdate().w("\n");
    w(" * @version   $Revision$ $Date$\n");
    w(" */\n");
    w("public ").w(classType).w(" ").w(name).w("\n");   
  }

  Obixc flush()
  {
    out.flush();
    return null;
  }

  Exception err(String msg)
  {
    return new Exception(msg); 
  }

////////////////////////////////////////////////////////////////
// Type
////////////////////////////////////////////////////////////////

  static class Type
  {          
    public String toString() { return name; }
    
    Obj proto;      
    Uri href;
    String name;     
    Type[] contracts;
    Child[] children;
  }

  static class Child
  {              
    Type parent;          // parent/container type    
    String name;          // name of child in parent type
    Obj obj;              // child Obj
    String className;     // Java class name
    String implClassName; // Java implementation class name
    String retClassName;  // in case covariant
  }

////////////////////////////////////////////////////////////////
// Main
////////////////////////////////////////////////////////////////

  public static void usage()
  {
    System.out.println("usage: obixc <src> <destDir> <package>");
    System.out.println("  Compiles obix prototypes into Java source files");
    System.out.println("Params:");
    System.out.println("  <src>      URI or file name for prototype XML file");
    System.out.println("  <destDir>  Directory to output .java files");
    System.out.println("  <package>  Package name for .java files");
  }

  public static void main(String args[])             
    throws Exception
  {           
    // command line params    
    String src;
    
    // parse command line into params              
    if (args.length < 3) { usage(); return; }
    src = args[0];     
    File destDir = new File(args[1]); destDir.mkdirs();
    String packageName = args[2];
    
    // map source to input stream as either filename or URL
    InputStream in;
    File file = new File(src);
    if (file.exists())
      in = new BufferedInputStream(new FileInputStream(file));
    else
      in = new BufferedInputStream(new URL(src).openStream());
    
    // run compiler  
    Obixc obixc = new Obixc(destDir, packageName);  
    obixc.compile(in);
  }
    
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  static boolean verbose;
  
  File destDir;    
  String packageName;
  String href;
  PrintWriter out;                  
  Type[] types;
  ArrayList contractHrefs   = new ArrayList();
  ArrayList contractClasses = new ArrayList();
  
}
