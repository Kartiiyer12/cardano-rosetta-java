package org.cardanofoundation.rosetta.api.util;

import static org.cardanofoundation.rosetta.api.util.Formatters.EMPTY_HEX;
import static org.cardanofoundation.rosetta.api.util.Formatters.hexStringFormatter;
import static org.cardanofoundation.rosetta.api.util.Formatters.isEmptyHexString;
import static org.cardanofoundation.rosetta.api.util.Formatters.remove0xPrefix;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FormattersTest {

  @Test
  void test_isEmptyHexString_true() {
    String toCheck = "\\x";

    assertTrue(isEmptyHexString(toCheck));
  }

  @Test
  void test_isEmptyHexString_false() {
    String toCheck = "isn't empty hex string";

    assertFalse(isEmptyHexString(toCheck));
  }

  @Test
  void test_isEmptyHexString_true_if_null() {
    String toCheck = null;

    assertTrue(isEmptyHexString(toCheck));
  }

  @Test
  void test_remove0xPrefix_with0x() {
    String hex = "0xc800e67d509ee0c6d6761f681245064458fad657dbf187d04aa9e16db6f9627e";

    assertFalse(remove0xPrefix(hex).startsWith("0x"));
  }

  @Test
  void test_remove0xPrefix_with_not_0x() {
    String hex = "c800e67d509ee0c6d6761f681245064458fad657dbf187d04aa9e16db6f9627e";

    assertFalse(remove0xPrefix(hex).startsWith("0x"));
  }

  @Test
  void test_hexStringFormatter_true() {
    String hexEmpty = "";

    assertEquals(EMPTY_HEX, hexStringFormatter(hexEmpty));
    assertEquals(EMPTY_HEX, hexStringFormatter(null));
  }
}