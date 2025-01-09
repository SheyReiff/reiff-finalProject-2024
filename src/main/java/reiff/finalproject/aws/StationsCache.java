package reiff.finalproject.aws;

import com.google.gson.Gson;
import reiff.finalproject.CitiBikeService;
import reiff.finalproject.Stations;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;

public class StationsCache {
    private static final String BUCKET_NAME = "reiff.citibike";
    private static final String STATION_INFO_KEY = "stationInformation.json";
    private static final Duration CACHE_DURATION = Duration.ofHours(1);

    private Stations stationsResponse;
    Instant lastModified;

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
        if (stationsResponse != null && isCacheValid()) {
            return stationsResponse;
        }

        if (stationsResponse == null || !isCacheValid()) {
            if (isS3DataRecent()) {
                readFromS3();
            } else {
                downloadAndCacheStations();
            }
        }

        return stationsResponse;
    }

    private boolean isCacheValid() {
        return Duration.between(lastModified,
                Instant.now()).compareTo(CACHE_DURATION) <= 0;
    }

    private void downloadAndCacheStations() {
        stationsResponse = service.stationsResponse().blockingGet();
        lastModified = Instant.now();
        writeToS3();
    }

    private void readFromS3() {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(STATION_INFO_KEY)
                    .build();

            InputStream inputStream = s3Client.getObject(getObjectRequest);
            stationsResponse = gson.fromJson(new InputStreamReader(inputStream), Stations.class);
            lastModified = getLastModifiedFromS3();
        } catch (Exception e) {
            e.printStackTrace();
            downloadAndCacheStations();
        }
    }

    private void writeToS3() {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(STATION_INFO_KEY)
                    .build();

            String jsonContent = gson.toJson(stationsResponse);
            s3Client.putObject(putObjectRequest, RequestBody.fromString(jsonContent));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Instant getLastModifiedFromS3() {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(STATION_INFO_KEY)
                    .build();
            HeadObjectResponse response = s3Client.headObject(headObjectRequest);
            return response.lastModified();
        } catch (Exception e) {
            return Instant.now();
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
