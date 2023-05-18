package org.cardanofoundation.rosetta.api.constructionApiService.impl;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.cardanofoundation.rosetta.crawler.model.SigningPayload;
import org.cardanofoundation.rosetta.crawler.model.rest.ConstructionDeriveResponse;
import org.cardanofoundation.rosetta.crawler.model.rest.ConstructionPayloadsRequest;
import org.cardanofoundation.rosetta.crawler.model.rest.ConstructionPayloadsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpServerErrorException;

class ConstructionApiDelegateImplPayloadTests extends IntegrationTest {

  private static final String INVALID_OPERATION_TYPE_ERROR_MESSAGE = "invalidOperationTypeError";

  private static final String BASE_DIRECTORY = "src/test/resources/files/construction/payload";

  @BeforeEach
  public void setUp() {
    baseUrl = baseUrl.concat(":").concat(serverPort + "").concat("/construction/payloads");
  }

  @Test
  void test_send_valid_input_and_output_operations() throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY + "/construction_payloads_request_valid.json"))),
        ConstructionPayloadsRequest.class);

    ConstructionPayloadsResponse response = restTemplate.postForObject(baseUrl,
        request, ConstructionPayloadsResponse.class);

    assertEquals(response.getPayloads().get(0).getAccountIdentifier().getAddress(),
        "addr1vxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7cpnkcpx");
    assertEquals(response.getPayloads().get(0).getHexBytes(),
        "333a6ccaaa639f7b451ce93764f54f654ef499fdb7b8b24374ee9d99eab9d795");
  }

  @Test
  void test_send_a_input_with_Byron_Address() throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY + "/construction_payloads_request_with_byron_input.json"))),
        ConstructionPayloadsRequest.class);

    ConstructionPayloadsResponse response = restTemplate.postForObject(baseUrl,
        request, ConstructionPayloadsResponse.class);

    assertEquals(response.getPayloads().get(0).getAccountIdentifier().getAddress(),
        "Ae2tdPwUPEZC6WJfVQxTNN2tWw4skGrN6zRVukvxJmTFy1nYkVGQBuURU3L");
    assertEquals(response.getPayloads().get(0).getHexBytes(),
        "333a6ccaaa639f7b451ce93764f54f654ef499fdb7b8b24374ee9d99eab9d795");
  }

  @Test
  void test_receive_single_payload_for_each_input_address() throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY + "/construction_payload_multiple_inputs.json"))),
        ConstructionPayloadsRequest.class);

    ConstructionPayloadsResponse response = restTemplate.postForObject(baseUrl,
        request, ConstructionPayloadsResponse.class);

    assertEquals(response.getPayloads().size(), 1);

  }

  @Test
  void test_return_an_error_when_operations_with_invalid_types() throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY + "/construction_payloads_invalid_operation_type.json"))),
        ConstructionPayloadsRequest.class);

    try {
      restTemplate.postForObject(baseUrl, request, ConstructionDeriveResponse.class);
      fail("Expected exception");
    } catch (HttpServerErrorException e) {
      String responseBody = e.getResponseBodyAsString();
      assertTrue(responseBody.contains(INVALID_OPERATION_TYPE_ERROR_MESSAGE));
      assertEquals(500, e.getRawStatusCode());
    }
  }

  @Test
  void test_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_stake_key_registration()
      throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY
                    + "/construction_payload_valid_operations_including_stake_key_registration.json"))),
        ConstructionPayloadsRequest.class);

    ConstructionPayloadsResponse response = restTemplate.postForObject(baseUrl,
        request, ConstructionPayloadsResponse.class);

    assertEquals(response.getPayloads().get(0).getAccountIdentifier().getAddress(),
        "addr1vxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7cpnkcpx");
    assertEquals(response.getPayloads().get(0).getHexBytes(),
        "ec6bb1091d68dcb3e4f4889329e143fbb6090b8e78c74e7c8d0903d9eec4eed1");
  }

  @Test
  void test_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_stake_delegation()
      throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY
                    + "/construction_payload_valid_operations_including_stake_delegation.json"))),
        ConstructionPayloadsRequest.class);

    ConstructionPayloadsResponse response = restTemplate.postForObject(baseUrl,
        request, ConstructionPayloadsResponse.class);

    assertEquals(response.getPayloads().size(), 2);
    String address1 = "stake1uxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7caek7a5";
    String address2 = "addr1vxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7cpnkcpx";

    String hexBytes = "ca12b42830eb7b53cf73c9f8b35875619a47e3e7569ebd13c3c309396ffc47d8";
    // Check for address1 and hexBytes
    boolean found1 = false;
    for (SigningPayload payload : response.getPayloads()) {
      if (payload.getAccountIdentifier().getAddress().equals(address1) &&
          payload.getHexBytes().equals(hexBytes)) {
        found1 = true;
        break;
      }
    }
    assertThat(found1).isTrue();

    // Check for address2 and hexBytes
    boolean found2 = false;
    for (SigningPayload payload : response.getPayloads()) {
      if (payload.getAccountIdentifier().getAddress().equals(address2) &&
          payload.getHexBytes().equals(hexBytes)) {
        found2 = true;
        break;
      }
    }
    assertThat(found2).isTrue();

  }

  @Test
  void test_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_stake_key_registration_stake_delegation()
      throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY
                    + "/construction_payload_valid_operations_including_stake_key_registration_and_stake_delegation.json"))),
        ConstructionPayloadsRequest.class);

    ConstructionPayloadsResponse response = restTemplate.postForObject(baseUrl,
        request, ConstructionPayloadsResponse.class);
    String address1 = "addr1vxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7cpnkcpx";
    String address2 = "stake1uxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7caek7a5";
    String hexBytes = "dbf6479409a59e3e99c79b9c46b6af714de7c8264094b1d38c373b7454acf33d";
    assertEquals(response.getPayloads().size(), 2);
    boolean found1 = false;
    for (SigningPayload payload : response.getPayloads()) {
      if (payload.getAccountIdentifier().getAddress().equals(address1) &&
          payload.getHexBytes().equals(hexBytes)) {
        found1 = true;
        break;
      }
    }
    assertThat(found1).isTrue();

    // Check for address2 and hexBytes
    boolean found2 = false;
    for (SigningPayload payload : response.getPayloads()) {
      if (payload.getAccountIdentifier().getAddress().equals(address2) &&
          payload.getHexBytes().equals(hexBytes)) {
        found2 = true;
        break;
      }
    }
    assertThat(found2).isTrue();

  }

  @Test
  void test_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_withdraw()
      throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY + "/construction_payload_valid_operations_including_withdrawal.json"))),
        ConstructionPayloadsRequest.class);

    ConstructionPayloadsResponse response = restTemplate.postForObject(baseUrl,
        request, ConstructionPayloadsResponse.class);

    assertEquals(response.getPayloads().size(), 2);
    assertEquals(response.getPayloads().get(0).getAccountIdentifier().getAddress(),
        "addr1vxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7cpnkcpx");
    assertEquals(response.getPayloads().get(0).getHexBytes(),
        "da2eb0d62aee9313fc68df0827bd176b55168bc9129aedce92f4e29b1d52de38");
    assertEquals(response.getPayloads().get(1).getAccountIdentifier().getAddress(),
        "stake1uxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7caek7a5");
    assertEquals(response.getPayloads().get(1).getHexBytes(),
        "da2eb0d62aee9313fc68df0827bd176b55168bc9129aedce92f4e29b1d52de38");

  }

  @Test
  void test_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_withdraw_and_stake_registration()
      throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY
                    + "/construction_payload_valid_operations_including_withdrwal_and_stake_registration.json"))),
        ConstructionPayloadsRequest.class);

    ConstructionPayloadsResponse response = restTemplate.postForObject(baseUrl,
        request, ConstructionPayloadsResponse.class);

    assertEquals(response.getPayloads().size(), 2);
    assertEquals(response.getPayloads().get(0).getAccountIdentifier().getAddress(),
        "addr1vxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7cpnkcpx");
    assertEquals(response.getPayloads().get(0).getHexBytes(),
        "8b47f0f3690167b596f1e7623e1869148f6bea78ebceaa08fe890a2e3e9e4d89");
    assertEquals(response.getPayloads().get(1).getAccountIdentifier().getAddress(),
        "stake1uxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7caek7a5");
    assertEquals(response.getPayloads().get(1).getHexBytes(),
        "8b47f0f3690167b596f1e7623e1869148f6bea78ebceaa08fe890a2e3e9e4d89");

  }

  @Test
  void test_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_stake_key_deregistration()
      throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY
                    + "/construction_payload_valid_operations_including_stake_delegation.json"))),
        ConstructionPayloadsRequest.class);

    ConstructionPayloadsResponse response = restTemplate.postForObject(baseUrl,
        request, ConstructionPayloadsResponse.class);

    assertEquals(response.getPayloads().size(), 2);
    assertEquals(response.getPayloads().get(0).getAccountIdentifier().getAddress(),
        "addr1vxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7cpnkcpx");
    assertEquals(response.getPayloads().get(0).getHexBytes(),
        "9c0f4e7fa746738d3df3665fc7cd11b2e3115e3268a047e0435f2454ed41fdc5");
    assertEquals(response.getPayloads().get(1).getAccountIdentifier().getAddress(),
        "stake1uxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7caek7a5");
    assertEquals(response.getPayloads().get(1).getHexBytes(),
        "9c0f4e7fa746738d3df3665fc7cd11b2e3115e3268a047e0435f2454ed41fdc5");

  }

  @Test
  void test_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_pool_retirement()
      throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY
                    + "/construction_payload_valid_operations_including_pool_retirement.json"))),
        ConstructionPayloadsRequest.class);

    ConstructionPayloadsResponse response = restTemplate.postForObject(baseUrl,
        request, ConstructionPayloadsResponse.class);

    assertEquals(response.getPayloads().size(), 2);
    assertEquals(response.getPayloads().get(0).getAccountIdentifier().getAddress(),
        "addr1vxa5pudxg77g3sdaddecmw8tvc6hmynywn49lltt4fmvn7cpnkcpx");
    assertEquals(response.getPayloads().get(0).getHexBytes(),
        "ec44114edfb063ce344797f95328ccfd8bc1c92f71816803803110cfebbb8360");
    assertEquals(response.getPayloads().get(1).getAccountIdentifier().getAddress(),
        "153806dbcd134ddee69a8c5204e38ac80448f62342f8c23cfe4b7edf");
    assertEquals(response.getPayloads().get(1).getHexBytes(),
        "ec44114edfb063ce344797f95328ccfd8bc1c92f71816803803110cfebbb8360");

  }

  @Test
  void test_should_throw_an_error_when_no_epoch_was_sent_on_pool_retirement_operation()
      throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY
                    + "/construction_payload_no_epoch_was_sent_on_pool_retirement_operation.json"))),
        ConstructionPayloadsRequest.class);

    try {
      restTemplate.postForObject(baseUrl, request, ConstructionDeriveResponse.class);
      fail("Expected exception");
    } catch (HttpServerErrorException e) {
      String responseBody = e.getResponseBodyAsString();
      assertTrue(responseBody.contains(INVALID_OPERATION_TYPE_ERROR_MESSAGE));
      assertEquals(500, e.getRawStatusCode());
    }
  }

  @Test
  void test_return_an_error_when_no_staking_key_is_provided_in_staking_key_registration_operation()
      throws IOException {
    ConstructionPayloadsRequest request = objectMapper.readValue(new String(Files.readAllBytes(
            Paths.get(
                BASE_DIRECTORY
                    + "/construction_payload_no_staking_key_is_provided_in_staking_key_registration.json"))),
        ConstructionPayloadsRequest.class);

    try {
      restTemplate.postForObject(baseUrl, request, ConstructionDeriveResponse.class);
      fail("Expected exception");
    } catch (HttpServerErrorException e) {
      String responseBody = e.getResponseBodyAsString();
      assertTrue(responseBody.contains(INVALID_OPERATION_TYPE_ERROR_MESSAGE));
      assertEquals(500, e.getRawStatusCode());
    }
  }
}

//  @Test
//  void test_return_an_error_when_staking_key_in_one_operation_has_invalid_format() {
//
//  }
//
//  @Test
//  void test_return_an_error_when_staking_key_in_one_operation_has_a_bigger_length_than_32() {
//
//  }
//
//  @Test
//  void test_return_an_error_when_staking_key_in_one_operation_has_a_smaller_length_than_32() {
//
//  }
//
//  @Test
//  void test_return_an_error_when_no_pool_key_hash_is_provided_for_stake_delegation() {
//
//  }
//
//  @Test
//  void test_return_an_error_when_an_invalid_pool_key_hash_is_provided_for_stake_delegation() {
//
//  }
//
//  @Test
//  void test_should_return_a_valid_unsigned_transaction_hash_when_sending_valid_operation_including_ma_amount() {
//
//  }
//
//  @Test
//  void test_should_fail_if_MultiAsset_policy_id_is_shorter_than_expected() {
//
//  }
//
//  @Test
//  void test_should_fail_if_MultiAsset_policy_id_is_not_a_hex_string() {
//
//  }
//
//  @Test
//  void test_should_fail_if_MultiAsset_policy_id_is_longer_than_expected() {
//
//  }
//
//  @Test
//  void test_should_fail_if_MultiAsset_symbol_is_not_a_hex_string() {
//
//  }
//
//  @Test
//  void test_should_fail_if_MultiAsset_symbol_longer_than_expected() {
//
//  }
//
//  @Test
//  void test_should_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_with_pool_registration_with_pledge() {
//
//  }
//
//  @Test
//  void test_should_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_pool_registration_with_Single_Host_Addr_relay() {
//
//  }
//
//  @Test
//  void test_should_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_pool_registration_with_Single_Host_Name_relay() {
//
//  }
//
//  @Test
//  void test_should_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_pool_registration_with_multi_host_name_relay() {
//
//  }
//
//  @Test
//  void test_should_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_pool_registration_with_no_pool_metadata() {
//
//  }
//
//  @Test
//  void test_should_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_including_pool_registration_with_multiple_relay() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_oprations_in() {
//
//  }
//
//  @Test
//  void test_should_throw_error_when_operations_include_pool_registration_with_invalid_cold_key_hash() {
//    // Test code here
//  }
//
//  @Test
//  void test_should_throw_error_when_operations_include_pool_registration_with_missing_cold_key_hash() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_empty_pool_relays() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_invalid_pool_relay_type() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_missing_pool_relay_type() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_invalid_pool_relays_with_invalid_ipv4() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_invalid_pool_relays_with_invalid_port() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_pool_relays_with_invalid_pool_metadata_hash() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_pool_relays_with_invalid_pool_owners() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_negative_cost() {
//
//  }
//
//  @Test
//  void should_throw_an_error_when_there_are_operations_including_pool_registration_with_pool_relays_with_negative_pledge() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_pool_relays_with_negative_pledge() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_pool_relays_with_negative_denominator() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_pool_relays_with_alphabetical_numerator() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_no_margin() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_are_operations_including_pool_registration_with_invalid_reward_address() {
//
//  }
//
//  @Test
//  void test_should_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_with_pool_registration_with_cert() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_sending_operations_with_pool_registration_with_invalid_cert() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_sending_operations_with_pool_registration_with_invalid_cert_type() {
//
//  }
//
//  @Test
//  void test_should_return_a_valid_unsigned_transaction_hash_when_sending_valid_operations_with_a_vote_registration() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_the_voting_key_is_empty() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_the_voting_key_is_not_valid() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_the_reward_address_is_empty() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_the_reward_address_is_not_valid() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_the_stake_key_is_empty() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_the_stake_key_is_not_valid() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_the_voting_nonce_is_not_greater_than_zero() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_the_voting_signature_is_empty() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_the_voting_signature_is_not_valid() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_the_transaction_has_no_metadata() {
//
//  }
//
//  @Test
//  void test_should_throw_an_error_when_there_is_no_vote_registration_metadata() {
//
//  }