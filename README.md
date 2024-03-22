# Amazon Connect KVS Consumer Demo

## Introduction
The Amazon Connect KVS Consumer Demo provides an example of how to process audio data published to a Kinesis Video Stream by Amazon Connect.

This code parses data from two tracks, namely `AUDIO_FROM_CUSTOMER` and `AUDIO_TO_CUSTOMER`, and places this data into 2 files (`AudioFromCustomer.raw` & `AudioToCustomer.raw`).

## Building from Source
Follow the below steps to deploy this solution;

* Install Amazon Corretto Java 11 from [here](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/what-is-corretto-11.html).
```shell
java -version
```
* Install Maven from [here](https://maven.apache.org/download.cgi).
```shell
mvn -version
```
* Install AWS CLI from [here](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html).
```shell
aws --version
```

Next, download the code from GitHub. You can build it using Maven via this command: `mvn clean install`.

## Execution
Following [this guide](https://docs.aws.amazon.com/connect/latest/adminguide/customer-voice-streams.html), enable `Live media streaming` on your Amazon Connect instance.

**Set Data Retention greater than 0 - this will store audio in the KVS stream such that it be retrieved by this demo.**

Create a Contact Flow containing the `Start/Stop Media Streaming` blocks. Also include a `Set contact attributes` block and create two User Defined attributes; 1 for `Customer audio start fragment number` and another for `Customer audio stream ARN`.

Place a call to this Contact Flow and find this contact in the `Contact Search` page. Expand the `Attributes` section and take note of the fragment number and stream arn.

Within the `LMSDemo.java`, populate the `<<StreamName>>` and `<<FragmentNumber>>` accordingly.

Next, get desired credentials and populate `<<AWSSessionToken>>`, `<<AWSAccessKey>>`, and `<<AWSSecretKey>>`.

Build your changes using `mvn clean install`.

Finally, the `LMSDemo` class can be executed locally using:
```
java -cp .\target\amazon-connect-kvs-consumer-demo-1.0.0-jar-with-dependencies.jar software/aws/connect/LMSDemo
```

This should produce 2 files, `AudioFromCustomer.raw` and `AudioToCustomer.raw`. To listen to the audio, open Audacity and select `File` > `Import` > `Raw Data`. Set Encoding to `Signed 16-bit PCM` and Sample rate to `8000 Hz`.

## Details
### Amazon Connect Demo
This demo consists of 3 files:
* `LMSDemo.java` - is a class with a main method that invokes LMSExample.
* `LMSExample.java` - is similar to the examples provided in the Kinesis Video Streams Parser library. It gets media from the specified Kinesis Video Streams with the specified fragment number. This code sample includes frame processing to separate the tracks.
* `LMSCompositeMkvElementVisitor.java` - extends [CompositeMkvElementVisitor.java](https://github.com/aws/amazon-kinesis-video-streams-parser-library/blob/master/src/main/java/com/amazonaws/kinesisvideo/parser/mkv/visitors/CompositeMkvElementVisitor.java) with a static `create()` method.
* `LMSTagVisitor.java` - extends [MkvElementVisitor.java](https://github.com/aws/amazon-kinesis-video-streams-parser-library/blob/master/src/main/java/com/amazonaws/kinesisvideo/parser/mkv/MkvElementVisitor.java) to print all tag element metadata (key-value pairs).
* `LMSDataVisitor.java` - extends [MkvElementVisitor.java](https://github.com/aws/amazon-kinesis-video-streams-parser-library/blob/master/src/main/java/com/amazonaws/kinesisvideo/parser/mkv/MkvElementVisitor.java) to save the `AUDIO_FROM_CUSTOMER` and `AUDIO_TO_CUSTOMER` tracks to disk.

## Release Notes

### Release 1.0.0 (March 2024)
* First release of the Amazon Connect Kinesis Video Streams consumer demo.
