package vc.signal.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matcher for blockchain addresses, such as the ones used in the Ethereum network.
 */
public class BlockchainAddressMatcher {

  private static final Pattern ETHEREUM_ADDRESS_EX = Pattern.compile("(?i)(0x)?[0-9a-f]{40}");
  private static final Pattern BITCOIN_ADDRESS_EX = Pattern.compile("[13][a-km-zA-HJ-NP-Z1-9]{25,34}");
  private static final Pattern NEO_ADDRESS_EX = Pattern.compile("(?i)A[0-9a-z]{33}");
  private static final Pattern RIPPLE_ADDRESS_EX = Pattern.compile("(?i)r[0-9a-z]{33}");

  /**
   * Matches blockchain addresses contained in <code>s</code>.
   *
   * @return list of all blockchain addresses found.
   */
  public static List<String> match(String s) {
    List<String> matchedAddresses = new ArrayList<>();
    for (Pattern pattern : Arrays.asList(ETHEREUM_ADDRESS_EX, BITCOIN_ADDRESS_EX, NEO_ADDRESS_EX)) {
      Matcher matcher = pattern.matcher(s);
      int cnt = 0;
      while (matcher.find()) {
        matchedAddresses.add(matcher.group());
        cnt++;
      }
      if (cnt > 0) {
        break;
      }
    }
    return matchedAddresses;
  }
}