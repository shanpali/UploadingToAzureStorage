import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

public class Main implements WatchingInputStream.ProgressListener {

    public static final String storageConnectionString =
            "DefaultEndpointsProtocol=https;" +
                    "AccountName=someaccountname;" +
                    "AccountKey=kjbdsgdvkdshighjdsnsdvjkdsvkjdhjkbvbfhvbfhbbhvsbhvbhvbsbvg==";


    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.upload();
    }

    public void upload() throws Exception
    {

        CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
        CloudBlobClient blobClient = account.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference("reports"
                + UUID.randomUUID().toString().replace("-", ""));

        // Create the container if it does not exist
        container.createIfNotExists();

        // Make the container public
        // Create a permissions object
        BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

        // Include public access in the permissions object
        containerPermissions
                .setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

        // Set the permissions on the container
        container.uploadPermissions(containerPermissions);

        CloudBlockBlob blob = container.getBlockBlobReference("dailyReport");
        File sourceFile = new File("./src/main/resources/testfile.txt");
        FileInputStream inputStream = new FileInputStream(sourceFile);
        WatchingInputStream watchingInputStream = new WatchingInputStream(inputStream, this);

        blob.upload(watchingInputStream, sourceFile.length());

        // Download the blob
        // For each item in the container
        for (ListBlobItem blobItem : container.listBlobs()) {
            // If the item is a blob, not a virtual directory
            if (blobItem instanceof CloudBlockBlob) {
                // Download the text
                CloudBlockBlob retrievedBlob = (CloudBlockBlob) blobItem;
                System.out.println(retrievedBlob.downloadText());
            }
        }

        // List the blobs in a container, loop over them and
        // output the URI of each of them
        for (ListBlobItem blobItem : container.listBlobs()) {
            System.out.println(blobItem.getUri().toString());
        }

//        // Delete the blobs
//        blob.deleteIfExists();
//
//        // Delete the container
//        container.deleteIfExists();

    }

    @Override
    public void onAdvance(long at, long length) {
        double percentage = (double)at / (double)length;
        System.out.println(percentage);
    }
}
