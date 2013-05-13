package obix.contracts;

import obix.*;

/**
 * Lobby
 *
 * @author    obix.tools.Obixc
 * @creation  24 May 06
 * @version   $Revision$ $Date$
 */
public interface Lobby
  extends IObj
{

  public static final String aboutContract = "<ref name='about' is='obix:About'/>";
  public Ref about();

  public static final String batchContract = "<op name='batch' in='obix:BatchIn' out='obix:BatchOut'/>";
  public Op batch();

  public static final String watchServiceContract = "<ref name='watchService' is='obix:WatchService'/>";
  public Ref watchService();

}
