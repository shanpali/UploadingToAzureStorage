package Utils;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class AzureHelper{

public void downLoadFromAzure(String Container, String BlobName) throws Exception
    {
        String storageConnectionString = storageConnectionString = String.format("DefaultEndpointsProtocol=https;" + "AccountName=%s;" + "AccountKey=%s", azureAccountName, azureAccessKey);
        CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
        CloudBlobClient blobClient = account.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference(helmContainer);

        CloudBlockBlob blob = container.getBlockBlobReference(BlobName+".tgz");
        String reportsLocation = System.getProperty( "user.dir" ) + "/temp/Charts.tgz";
        String extractLocation = System.getProperty( "user.dir" ) + "/temp";
        if (blob.exists()){
            blob.downloadToFile(reportsLocation);
            File tgzFile = new File(reportsLocation);
            File destFile = new File(extractLocation);
            util.extractGZip(tgzFile,destFile);
        }
    }
}
