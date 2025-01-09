package reiff.finalproject.aws;

import com.google.gson.Gson;
import reiff.finalproject.CitiBikeService;
import reiff.finalproject.Stations;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;

public class StationsCache {
    private static final String BUCKET_NAME = "reiff.citibike";
    private static final String STATION_INFO_KEY = "stationInformation.json";
    private static final Duration CACHE_DURATION = Duration.ofHours(1);
    private Stations stationsResponse;
    private Instant lastModified;
    private final Gson gson = new Gson();
    private final S3Client s3Client;
    private final CitiBikeService service;

    public StationsCache(CitiBikeService service) {

        this.service = service;
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public Stations getStations() {

        System.out.println("Started getStations");
        if (stationsResponse != null && Duration.between(lastModified,
                Instant.now()).compareTo(CACHE_DURATION) <= 0) {
            return stationsResponse;

        } else if (stationsResponse != null && Duration.between(lastModified,
                Instant.now()).compareTo(CACHE_DURATION) > 0) {
            lastModified = Instant.now();
            writeToS3();
        }

        if (stationsResponse == null && isS3DataRecent()) {
            readFromS3();
            lastModified = getLastModifiedFromS3();

        } else if (stationsResponse == null && !isS3DataRecent()) {
            lastModified = Instant.now();
            writeToS3();
        }


        System.out.println("Ended getStations");
        return stationsResponse;
    }

    private void readFromS3() {
        System.out.println("Started readfroms3");
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest
                    .builder()
                    .bucket(BUCKET_NAME)
                    .key(STATION_INFO_KEY)
                    .build();

            InputStream inputStream = s3Client.getObject(getObjectRequest);
            System.out.println("Ended readfroms3");
            stationsResponse = gson.fromJson(new InputStreamReader(inputStream), Stations.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToS3() {
        System.out.println("Started writetos3");

        try {
            stationsResponse = service.statusResponse().blockingGet();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(STATION_INFO_KEY)
                    .build();

            String jsonContent = gson.toJson(stationsResponse);
            s3Client.putObject(putObjectRequest, RequestBody.fromString(jsonContent));
            System.out.println("Ended writes3");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Instant getLastModifiedFromS3() {
        System.out.println("Started lm from s3");
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(STATION_INFO_KEY)
                .build();

        try {
            HeadObjectResponse response = s3Client.headObject(headObjectRequest);
            System.out.println("Ended lm from s3");
            return response.lastModified();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isS3DataRecent() {
        try {
            Instant s3LastModified = getLastModifiedFromS3();
            return s3LastModified != null && Duration.between(s3LastModified,
                    Instant.now()).compareTo(CACHE_DURATION) < 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}