package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.clients.ApiClient;
import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.model.*;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.enums.ReportTypeEnum;
import cn.amazon.aws.rp.spapi.utils.CredentialsHelper;
import cn.amazon.aws.rp.spapi.utils.DateUtil;
import cn.amazon.aws.rp.spapi.utils.Utils;
import com.sun.tools.javac.util.List;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ReportsApiIT {

    static private final Logger logger = LoggerFactory.getLogger(ReportsApiIT.class);

    private ReportsApi initWithCredentials() throws NoSuchFieldException, IllegalAccessException {
        SellerCredentials credentials = CredentialsHelper.getSellerCredentials();
        return ReportsApi.buildReportsApi(credentials);
    }
    @Test
    void cancelReportCall() {
    }

    @Test
    void cancelReport() {
    }

    @Test
    void cancelReportWithHttpInfo() {
    }

    @Test
    void cancelReportAsync() {
    }

    @Test
    void cancelReportScheduleCall() {
    }

    @Test
    void cancelReportSchedule() {
    }

    @Test
    void cancelReportScheduleWithHttpInfo() {
    }

    @Test
    void cancelReportScheduleAsync() {
    }

    @Test
    void createReportCall() {
    }

    @Test
    void createReport() throws NoSuchFieldException, IllegalAccessException, ApiException {
        ReportsApi reportsApi = initWithCredentials();
        CreateReportSpecification createReportSpecification = new CreateReportSpecification();
//        createReportSpecification.setDataStartTime(DateUtil.getOf("2020-09-01 03:22:53"));
//        createReportSpecification.setDataEndTime(DateUtil.getOf("2020-09-30 03:22:53"));
        createReportSpecification.setReportType(ReportTypeEnum.GET_FBA_MYI_UNSUPPRESSED_INVENTORY_DATA.name());
        createReportSpecification.setMarketplaceIds(List.of("ATVPDKIKX0DER"));
        CreateReportResponse reportResponse = reportsApi.createReport(createReportSpecification);
        System.out.println(reportResponse.getPayload());

//        155233018653
    }

    @Test
    void createReportWithHttpInfo() {
    }

    @Test
    void createReportAsync() {
    }

    @Test
    void createReportScheduleCall() {
    }

    @Test
    void createReportSchedule() {
    }

    @Test
    void createReportScheduleWithHttpInfo() {
    }

    @Test
    void createReportScheduleAsync() {
    }

    @Test
    void getReportCall() {
    }

    @Test
    void getReport() throws NoSuchFieldException, IllegalAccessException, ApiException {
        ReportsApi reportsApi = initWithCredentials();

        GetReportResponse reportResponse = reportsApi.getReport("53864018677");
        logger.info("payload is {}",reportResponse.getPayload());

    }

    @Test
    void getReportWithHttpInfo() {
    }

    @Test
    void getReportAsync() {
    }

    @Test
    void getReportDocumentCall() {
    }

    @Test
    void getReportDocument() throws NoSuchFieldException, IllegalAccessException, ApiException {
        ReportsApi reportsApi = initWithCredentials();
        GetReportDocumentResponse reportDocument = reportsApi.getReportDocument("amzn1.tortuga.3.7a74b81e-936f-459e-8fdd-44b032f29a66.T3U3FWBY2DYV4F");
        System.out.println(reportDocument.getPayload());

    }

    @Test
    void downloadAndDecryptReport() throws NoSuchFieldException, IllegalAccessException, ApiException {
        ReportsApi reportsApi = initWithCredentials();
        GetReportDocumentResponse reportDocument = reportsApi.getReportDocument("amzn1.tortuga.3.7a74b81e-936f-459e-8fdd-44b032f29a66.T3U3FWBY2DYV4F");
        String getCompressionAlgorithm = reportDocument.getPayload().getCompressionAlgorithm() == null? null:reportDocument.getPayload().getCompressionAlgorithm().getValue();
        Utils.downloadAndDecrypt(reportDocument.getPayload().getEncryptionDetails().getKey(),
                reportDocument.getPayload().getEncryptionDetails().getInitializationVector(),
                reportDocument.getPayload().getUrl(),
                getCompressionAlgorithm);
    }

    @Test
    void getReportDocumentWithHttpInfo() {
    }

    @Test
    void getReportDocumentAsync() {
    }

    @Test
    void getReportScheduleCall() {
    }

    @Test
    void getReportSchedule() {
    }

    @Test
    void getReportScheduleWithHttpInfo() {
    }

    @Test
    void getReportScheduleAsync() {
    }

    @Test
    void getReportSchedulesCall() {
    }

    @Test
    void getReportSchedules() throws NoSuchFieldException, IllegalAccessException, ApiException {
        ReportsApi reportsApi = initWithCredentials();
//        GetReportSchedulesResponse response = reportsApi.getReportSchedules(List.of(ReportTypeEnum.GET_AMAZON_FULFILLED_SHIPMENTS_DATA_GENERAL.name()));
        GetReportSchedulesResponse response = reportsApi.getReportSchedules(List.of("GET_FLAT_FILE_ALL_ORDERS_DATA_BY_LAST_UPDATE"));

        System.out.println(response.getPayload());
    }

    @Test
    void getReportSchedulesWithHttpInfo() {
    }

    @Test
    void getReportSchedulesAsync() {
    }

    @Test
    void getReportsCall() {
    }

    @Test
    void getReports() throws NoSuchFieldException, IllegalAccessException, ApiException {
        ReportsApi reportsApi = initWithCredentials();
        GetReportsResponse reportsResponse = reportsApi.getReports(
                List.of(ReportTypeEnum.GET_FLAT_FILE_ALL_ORDERS_DATA_BY_LAST_UPDATE.name())
                ,null,null,10,DateUtil.getOf("2021-01-26 03:22:53"),null,null);
        System.out.println(reportsResponse.getPayload());
    }

    @Test
    void getReportsWithHttpInfo() {
    }

    @Test
    void getReportsAsync() {
    }

    @Test
    void buildReportsApi() {
    }
}