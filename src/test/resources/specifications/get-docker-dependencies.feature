Feature: Get Helm Dependencies

    Scenario: Can get all Docker dependencies names
        Given a packaged Helm Chart keycloak-15.1.2.tgz
        When the Docker dependencies action is triggered
        Then Docker dependency names postgresql,keyloak are received