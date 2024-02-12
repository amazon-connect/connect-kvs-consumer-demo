package software.aws.connect;

import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.AWSSessionCredentialsProvider;
import com.amazonaws.regions.Regions;
import software.aws.connect.LMSExample;

import java.io.FileOutputStream;
import java.io.IOException;

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

public class LMSDemo {

    public static void main(String args[]) throws InterruptedException, IOException {
        LMSExample example = new LMSExample(
            Regions.US_WEST_2,
            "<<StreamName>>",
            "<<FragmentNumber>>",
            new AWSSessionCredentialsProvider() {
                @Override
                public AWSSessionCredentials getCredentials() {
                    return new AWSSessionCredentials() {
                        @Override
                        public String getSessionToken() {
                            return "<<AWSSessionToken>>";
                        }

                        @Override
                        public String getAWSAccessKeyId() {
                            return "<<AWSAccessKey>>";
                        }

                        @Override
                        public String getAWSSecretKey() {
                            return "<<AWSSecretKey>>";
                        }
                    };
                }

                @Override
                public void refresh() {}
            },
            new FileOutputStream("AudioFromCustomer.raw"),
            new FileOutputStream("AudioToCustomer.raw")
        );

        example.execute();
    }
}