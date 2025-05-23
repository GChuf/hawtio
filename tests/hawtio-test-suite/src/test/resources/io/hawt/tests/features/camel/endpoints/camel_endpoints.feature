Feature: Checking the functionality of Camel Endpoints page.

  Scenario Outline: Check that Endpoints table on Camel Endpoints page is not empty
    Given User is on "Camel" page
    When User is on Camel "endpoints" folder of "SampleCamel" context
    Then Camel table "<column>" column is not empty
    And Camel table has "<uri>" key and "<state>" value

    Examples: Columns
      | column | uri           | state   |
      | URI    | mock://result | Started |
      | State  | stream://out  | Started |

  Scenario: Check to add an Endpoint from URI
    Given User is on "Camel" page
    And User is on Camel "endpoints" folder of "SampleCamel" context
    When User adds Endpoint "timer://foo?period=1000" from URI
    And Successful alert message is appeared and closed
    Then User is on Camel "endpoints" folder of "SampleCamel" context
    And Camel table has "timer://foo?period=1000" key and "Started" value

  Scenario: Check to add an Endpoint from Data
    Given User is on "Camel" page
    And User is on Camel "endpoints" folder of "SampleCamel" context
    When User adds Endpoint "bar" name and "mock" component from Data
    And Successful alert message is appeared and closed
    Then User is on Camel "endpoints" folder of "SampleCamel" context
    And Camel table has "mock://bar" key and "Started" value
