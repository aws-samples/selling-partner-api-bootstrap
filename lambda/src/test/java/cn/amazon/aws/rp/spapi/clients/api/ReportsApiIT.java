package cn.amazon.aws.rp.spapi.clients.api;

import cn.amazon.aws.rp.spapi.clients.ApiException;
import cn.amazon.aws.rp.spapi.clients.model.*;
import cn.amazon.aws.rp.spapi.dynamodb.entity.SellerCredentials;
import cn.amazon.aws.rp.spapi.enums.ReportTypeEnum;
import cn.amazon.aws.rp.spapi.utils.DateUtil;
import cn.amazon.aws.rp.spapi.utils.Utils;
import com.sun.tools.javac.util.List;
import org.junit.jupiter.api.Test;
import sun.security.util.ArrayUtil;

import static org.junit.jupiter.api.Assertions.*;

class ReportsApiIT {

    private ReportsApi initWithCredentials() throws NoSuchFieldException, IllegalAccessException {
        SellerCredentials credentials = new SellerCredentials();
//        credentials.setLWAAuthorizationCredentials_refreshToken("Atzr|IwEBIJ27mtx3w0pHV9Rc8TLfmcGX2yEQnC27-88Ya_uI8FqOdAXrdCTzJucIhj1nc-XHkHNxRbBosXdF33nJtDYOQYvql_FGwYBmPMAmu24YybdD3BblXut81LxL6HKTzfF2Ebgi_lF-KmHSxoz4glZCgH8a-2jbOZJbnvJKb_bAZLxWfsgLawhqlHrhyhpSoCAclVfvFGzWG2Wv1hJDgSV2ggMf-4Y26TJ58rM-gMLuL4ipjeOG7QWb7pLcdgcly5XiMuLJLGNVf8h_1-OznfgFgnroYrORlRRkCQkfdheDO_BT0BNj0GPm3bX5u3wsY9go4To");
        credentials.setLWAAuthorizationCredentials_refreshToken("Atzr|IwEBIBOvQp_eXz1bdpUVtjNX7C_cEp50Z6uQ5iQbv-VQOY7H5AYlB5DR87K73AH_oNJc0DSD0wDqNaDFNxVjVbv0forJR4ti8XVZF7VXUQJxNAWjlByAERRO3QXqItyaetkBLhlLdGdVHQ3bzXtVkvRZmYqOivnSz9gRNxkUMdMQu9vPRZWNgitX-oVgSTfS-mJzeeEpiynWfviMwKRB9sfcGvQLM17NME6lFjA0-OnQgVh-8wQiolrNRzoeupVeud7eXbgB17YaM5Bk6XhtLAWSJCL5oYqN6dS6OVYWQiqrPShlCMu2ot1mOf9uRwfVhXOfy-Y");
        credentials.setSeller_id("seller_jim");

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
        createReportSpecification.setDataStartTime(DateUtil.getOf("2020-09-01 03:22:53"));
        createReportSpecification.setDataEndTime(DateUtil.getOf("2020-09-30 03:22:53"));
        createReportSpecification.setReportType(ReportTypeEnum.GET_FLAT_FILE_OPEN_LISTINGS_DATA.name());
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
        GetReportResponse reportResponse = reportsApi.getReport("52138018653");
        System.out.println(reportResponse.getPayload());

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
        GetReportDocumentResponse reportDocument = reportsApi.getReportDocument("amzn1.tortuga.3.c84f19b7-59ff-46b5-9fbf-62042c31a3c8.T1XDH2W583L6U4");
        System.out.println(reportDocument.getPayload());
    }

    @Test
    void downloadAndDecryptReport() throws NoSuchFieldException, IllegalAccessException, ApiException {
        ReportsApi reportsApi = initWithCredentials();
        GetReportDocumentResponse reportDocument = reportsApi.getReportDocument("amzn1.tortuga.3.88855d45-71dc-4f18-b2ca-f688240c8e78.T12YO8GXFAY986");
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