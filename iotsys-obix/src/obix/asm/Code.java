/*
 * This code licensed to public domain
 */
package obix.asm;

import java.lang.reflect.*;

/**
 * @author    Brian Frank
 * @creation  15 Mar 00
 * @version   $Revision: 1$ $Date: 6/21/00 2:32:06 PM$
 */
public class Code
  extends AttributeInfo
{  

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  public Code(Assembler asm)
  {
    super(asm, Jvm.ATTR_CODE);
    this.cp = asm.cp;
  }

////////////////////////////////////////////////////////////////
// Add
////////////////////////////////////////////////////////////////
  
  public int add(int opcode)
  {
    if (Jvm.OPCODE_ARGS[opcode] != Jvm.NONE) 
      throw new IllegalStateException("Opcode requires arguments: " + opcode);
    return code.u1(opcode);
  }

  public int add(int opcode, int arg)
  {
    int ref = code.count;
     
    // auto widen LDC opcode if necessary
    if (opcode == Jvm.LDC)
    {
      if (arg < 255) { code.u1(Jvm.LDC); code.u1(arg); }
      else { code.u1(Jvm.LDC_W); code.u2(arg); }
      return ref;
    }
    
    int argType = Jvm.OPCODE_ARGS[opcode];
    code.u1(opcode);
    if (argType == Jvm.U1) code.u1(arg);
    else if (argType == Jvm.U2) code.u2(arg);
    else throw new IllegalStateException("Opcode does not take u1 or u2 args: " + opcode);
    return ref;
  }
    
  public int addIntConst(int v)
  {
    // if the constant is between -1 to 5,
    // we can use the iconst_x operations
    // for maximum efficiency
    switch(v)
    {
      case -1: return add( Jvm.ICONST_M1 );
      case 0:  return add( Jvm.ICONST_0 );
      case 1:  return add( Jvm.ICONST_1 );
      case 2:  return add( Jvm.ICONST_2 );
      case 3:  return add( Jvm.ICONST_3 );
      case 4:  return add( Jvm.ICONST_4 );
      case 5:  return add( Jvm.ICONST_5 );
    }

    // if the constant fits in a byte use bipush
    if (Byte.MIN_VALUE <= v && v <= Byte.MAX_VALUE)
    {
      return add( Jvm.BIPUSH, v );
    }

    // if the constant fits in two bytes use sipush
    if (Short.MIN_VALUE <= v && v <= Short.MAX_VALUE)
    {
      return add( Jvm.SIPUSH, v );
    }

    // else we have to load from the constant pool
    return add( Jvm.LDC, cp.integer(v) );
  }
  
  public int addPad(int opcode)
  {
    int ref = code.u1(opcode);
    int pad = 3 - ref % 4;
    for(int i=0; i<pad; ++i) code.u1(0);
    return ref;
  }
  
  public int invoke(Method m)
  {
    int method = cp.method(m);
    int flags = m.getModifiers();
    
    if (Modifier.isInterface(flags)) 
      return invokeInterface(method, m.getParameterTypes().length+1);
    else if (Modifier.isStatic(flags)) 
      return add(Jvm.INVOKESTATIC, method);
    else 
      return add(Jvm.INVOKEVIRTUAL, method);
  }

  public int invokeInterface(int method, int nargs)
  {
    int ref = code.count;
    code.u1(Jvm.INVOKEINTERFACE);
    code.u2(method);
    code.u1(nargs);
    code.u1(0);
    return ref;
  }

////////////////////////////////////////////////////////////////
// Compiler
////////////////////////////////////////////////////////////////

  void compile(Buffer buf)
  {
    int len = 8 + code.count + 4;
    
    buf.u2(name);          // attribute name
    buf.u4(len);           // attribute length
    buf.u2(maxStack);      // max stack
    buf.u2(maxLocals);     // max locals
    buf.u4(code.count);    // code length
    buf.append(code);      // code
    buf.u2(0);             // exceptions not supported
    buf.u2(0);             // attributes not supported
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  public final ConstantPool cp;
  public int maxStack  = 10;
  public int maxLocals = 10;
  public Buffer code = new Buffer(512);
  
}
