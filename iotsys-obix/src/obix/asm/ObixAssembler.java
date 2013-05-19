/*
 * This code licensed to public domain
 */
package obix.asm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import obix.ContractRegistry;
import obix.IObj;

/**          
 * ObixAssembler uses the Java bytecode assembly library
 * to dynamically generate classes which extend from one
 * of the core base types (Obj, Bool, Real, etc) and also
 * implement a list of interfaces to model a contract.
 *
 * @author    Brian Frank
 * @creation  24 May 06
 * @version   $Revision$ $Date: 9/9/2003 3:39:17 PM$
 */
public class ObixAssembler  
  extends Assembler     
  implements OpCodes
{
  
  /**
   * Compile a class which extends from superClass, and implements
   * all the interfaces.  Each interface must contain only obix
   * getter methods defining a contracts sub-objects.
   */
  public static Class compile(Class superClass, Class[] interfaces)
    throws Exception
  {   
    // short circuit if no interfaces to add                                     
    if (interfaces.length == 0)
      return superClass;
      
    // generate a unique name             
    String name = null;             
    synchronized (nameLock)
    { 
      name = "obix$" + (nextName++);
    }                        
    
    // create assembler
    ObixAssembler asm = new ObixAssembler(name, superClass, interfaces);
    
    // init contructor
    Code ctor = new Code(asm); 
    ctor.add(ALOAD_0);
    ctor.add(INVOKESPECIAL, asm.cp.method(asm.superClass, "<init>", "()V"));
    
    // walk thru each interface and add code 
    // to ctor and accessor methods
    HashMap done = new HashMap();
    for (int i=0; i<interfaces.length; ++i)
      asm.compileInterface(interfaces[i], done, ctor);
    
    // add ctor
    ctor.add(RETURN);
    asm.addMethod(new MethodInfo(asm, "<init>", "()V", Jvm.ACC_PUBLIC, ctor));
    
    // compile to a class file
    Buffer classFile = asm.compile();
    
    // put class file into a table for classloader, then
    // use our custom classloader to defint it
    synchronized(loadLock) { loadClassFiles.put(name, classFile); }
    return classLoader.loadClass(name);
  }   
  
  /**
   * For the specified interface:
   *  1) look at methods to figure out contract children
   *  2) add child init in constructor
   *  3) add accessor method
   */
  private void compileInterface(Class iface, HashMap done, Code ctor)
  {                       
    // sanity checks
    if (!iface.isInterface() || !IObj.class.isAssignableFrom(iface))
      throw new IllegalArgumentException("Invalid contract interface: + " + iface);

    // process each method    
    Method[] methods = iface.getMethods();
    for (int i=0; i<methods.length; ++i)
    {              
      // process next method
      Method m = methods[i];   
      
      // skip anything from IObj
      if (m.getDeclaringClass() == IObj.class) continue;
      
      // skip if already done, otherwise not it is done
      if (done.get(m.getName()) != null) continue;
      done.put(m.getName(), m);
      
      // process this method
      compileSubObject(ctor, m);
    }
  }
  
  /**
   * Process a single method which should represent a single
   * sub-object from a contract.
   */
  private void compileSubObject(Code ctor, Method m)
  {                  
    // gather some initial info
    String name = m.getName();        
    Class type = m.getReturnType();
    
    // anything with parameters or that doesn't
    // return an Obj is invalid      
    if (m.getParameterTypes().length != 0 || 
        !IObj.class.isAssignableFrom(type))
      throw new IllegalArgumentException("Incorrect contract method: " + m);
   
    // check if there is a contract field that we can
    // use to initialize the child object - if a contract 
    // field is specified it must be a string constant with 
    // the actual oBIX XML used to initialize the field; an
    // example from obix:Unit:
    //
    //   interface Unit extends IObj
    //   {
    //     public static final String dimensionContract = "<obj name='dimension' is='obix:Dimension'/>";
    //     public Dimension dimension();
    //   }
    String contract = null;
    try
    {
      Field field = m.getDeclaringClass().getField(name + "Contract");
      contract = (String)field.get(null);
    }
    catch(Exception e)
    {             
    }
         
    // add some items to constant pool
    int cpType = cp.cls(type);
    int cpName = cp.string(name);
    
    // add as child to the ctor 
    if (contract == null)
    {                 
      // if a contract field wasn't specified, then we
      // assume this is a simple value type with no special 
      // facets or extended contract - so we can use a simple 
      // no arg constructor for the proper type
      ctor.add(ALOAD_0);    
      ctor.add(LDC, cpName);      
      ctor.add(NEW, cpType);
      ctor.add(DUP);
      ctor.add(INVOKESPECIAL, cp.method(cpType, "<init>", "()V"));
      ctor.add(INVOKEVIRTUAL, objAddWithName());
      ctor.add(POP); // add returns this
    } 
    else
    { 
      // this is the simplest, but not the fastest way       
      // way to initialize a complicated child - by analyzing
      // the contract XML we could "unroll" the parser to
      // as series of setVal, setMin, etc calls; but I'll leave
      // that to some future aspiring  hacker
      ctor.add(ALOAD_0);    
      ctor.add(LDC, cp.string(contract));      
      ctor.add(INVOKESTATIC, decoderFromString());
      ctor.add(INVOKEVIRTUAL, objAdd());
      ctor.add(POP); // add returns this
    }
    
    // add accessor method
    Code getter = new Code(this);
    getter.add(ALOAD_0);
    getter.add(LDC, cpName);
    getter.add(INVOKEVIRTUAL, objGet());
    getter.add(CHECKCAST, cpType);
    getter.add(ARETURN);
    addMethod(new MethodInfo(this, name, "()L" + type.getName().replace('.', '/') + ";", Jvm.ACC_PUBLIC, getter));
  }

////////////////////////////////////////////////////////////////
// Private Constructor
////////////////////////////////////////////////////////////////

  private ObixAssembler(String thisClass, Class superClass, Class[] interfaces)
  {                          
    super(thisClass, superClass, Jvm.ACC_PUBLIC, interfaces);
  }

////////////////////////////////////////////////////////////////
// Method Utils 
////////////////////////////////////////////////////////////////

  private int objAdd()
  {            
    if (objAdd == 0)
      objAdd = cp.method("obix/Obj", "add", "(Lobix/Obj;)Lobix/Obj;");
    return objAdd;
  }

  private int objAddWithName()
  {            
    if (objAddWithName == 0)
      objAddWithName = cp.method("obix/Obj", "add", "(Ljava/lang/String;Lobix/Obj;)Lobix/Obj;");
    return objAddWithName;
  }

  private int objGet()
  {            
    if (objGet == 0)        
      objGet = cp.method("obix/Obj", "get", "(Ljava/lang/String;)Lobix/Obj;");    
    return objGet;
  }

  private int decoderFromString()
  {            
    if (decoderFromString == 0)        
      decoderFromString = cp.method("obix/io/ObixDecoder", "fromString", "(Ljava/lang/String;)Lobix/Obj;");    
    return decoderFromString;
  }

////////////////////////////////////////////////////////////////
// AsmClassLoader
////////////////////////////////////////////////////////////////

  static class AsmClassLoader
    extends ClassLoader
  {                          
    AsmClassLoader()
    {
      super(ContractRegistry.getContractClassLoader());
    }
    
    public Class findClass(String name) 
      throws ClassNotFoundException
    {          
      // first check if this is code in our class file map,
      // if not then get safe copy of dependencies
      Buffer classFile = null;                                
      synchronized(loadLock)
      {
        classFile = (Buffer)loadClassFiles.get(name);
        if (classFile != null) loadClassFiles.remove(name);
      }
      
      // if predefined, then use it
      if (classFile != null)
        return defineClass(name, classFile.bytes, 0, classFile.count);
        
      // otherwise, my parent class loader should have found it            
      throw new ClassNotFoundException(name);
    }
  }

////////////////////////////////////////////////////////////////
// Main 
////////////////////////////////////////////////////////////////

  /*
  public static interface A extends IObj {}  
  public static interface B extends IObj 
  {
    Str foo();
    Bool enabled();
  }  

  public static void main(String[] args)
    throws Exception
  {                           
    Class cls = compile(obix.Real.class, new Class[] { A.class, B.class });
    System.out.println("  class   " + cls);
    System.out.println("  extends " + cls.getSuperclass().getName());
    Class[] interfaces = cls.getInterfaces();
    for (int i=0; i<interfaces.length; ++i)
      System.out.println("  implements " + interfaces[i].getName());
    
    Real obj = (Real)cls.newInstance(); 
    obj.dump();                          
    B b = (B)obj;
    
    System.out.println(" enabled: " + b.enabled());
    System.out.println(" foo:     " + b.foo());
    b.foo().setStr("rocking!");
    System.out.println(" foo:     " + b.foo());
  }     
  */
  
////////////////////////////////////////////////////////////////
// Fields 
////////////////////////////////////////////////////////////////

  static AsmClassLoader classLoader = new AsmClassLoader();
  static final Object nameLock = new Object();
  static final Object loadLock = new Object();
  static HashMap loadClassFiles = new HashMap(); // className -> byte[]
  static int nextName = 0;
  
  private int objAdd, objAddWithName, objGet, decoderFromString;

}

