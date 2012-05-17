/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brief;

/**
 *
 * @author cfmak
 */
public class DescriptorMismatchException extends RuntimeException {
  public DescriptorMismatchException(int len1, int len2) {
    super("Comparing two descriptors of different length:"+len1+" "+len2);
  }
}