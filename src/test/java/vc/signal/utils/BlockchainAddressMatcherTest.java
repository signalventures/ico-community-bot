package vc.signal.utils;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @see BlockchainAddressMatcher
 */
public class BlockchainAddressMatcherTest {

  @Test
  public void testClearMessage() {
    String s = "I <3 the 0x project.";
    List<String> matchedAddresses = BlockchainAddressMatcher.match(s);
    assertTrue(matchedAddresses.isEmpty());
  }

  @Test
  public void testSingleEthereumAddressx() {
    String s = "Please send all your ether to A2uiinAGcG5pUkAWl9ZxHsZRqfCjbBUna.";
    List<String> matchedAddresses = BlockchainAddressMatcher.match(s);
    assertEquals(matchedAddresses.size(), 1);
    assertEquals(matchedAddresses.get(0), "A2uiinAGcG5pUkAWl9ZxHsZRqfCjbBUna");
  }

  @Test
  public void testSingleEthereumAddress() {
    String s = "Please send all your ether to 0xab5801a7d398351b8be11c439e05c5b3259aec9b.";
    List<String> matchedAddresses = BlockchainAddressMatcher.match(s);
    assertEquals(matchedAddresses.size(), 1);
    assertEquals(matchedAddresses.get(0), "0xab5801a7d398351b8be11c439e05c5b3259aec9b");
  }

  @Test
  public void testMultipleEthereumAddresses() {
    String s = "Please send all your ether to 0xab5801a7d398351b8be11c439e05c5b3259aec9b and 0xAB5801a7d398351b8be11c439e05c5b3259aec9A.";
    List<String> matchedAddresses = BlockchainAddressMatcher.match(s);
    System.out.println(matchedAddresses);
    assertEquals(matchedAddresses.size(), 2);
    assertEquals(matchedAddresses.get(0), "0xab5801a7d398351b8be11c439e05c5b3259aec9b");
    assertEquals(matchedAddresses.get(1), "0xAB5801a7d398351b8be11c439e05c5b3259aec9A");
  }

  @Test
  public void testNeoAddress() {
    String s = "Please send all your NEO to AU4skhNKRzHKPTg7pLMnGKjMSGAFL9XSuc.";
    List<String> matchedAddresses = BlockchainAddressMatcher.match(s);
    assertEquals(matchedAddresses.size(), 1);
    assertEquals(matchedAddresses.get(0), "AU4skhNKRzHKPTg7pLMnGKjMSGAFL9XSuc");
  }
}
