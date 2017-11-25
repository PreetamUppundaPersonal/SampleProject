package co.dlg.test.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.json.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceUtil {

    public JSONObject getMainContact(boolean policyHolder) {

        JSONObject mainContact = new JSONObject();
        mainContact.put("subtype", "Person")
                .put("firstName", "first name")
                .put("lastName", "last name");
        if (policyHolder) {
            mainContact.put("displayName", "test1 test2")
                    .put("homeNumber", "01111111111")
                    .put("emailAddress1", "asd@asd.com")
                    .put("contactType", "Person");
        }
        return mainContact;
    }

    public JSONArray getRelatedContact(boolean policyHolder) {

        JSONArray relatedContact = new JSONArray();
        if (!policyHolder) {
            relatedContact.put(new JSONObject().put("role", "witness")
                    .put("injured", false)
                    .put("contact", new JSONObject()
                            .put("displayName", "asd asd")
                            .put("firstName", "asd")
                            .put("lastName", "asd")
                            .put("homeNumber", "01111111111")
                            .put("emailAddress1", "asd@asd.com")
                            .put("contactType", "Person")));
        }
        return relatedContact;
    }

    public JSONObject getClaimQuestionCommonFF(String mitigationAction, boolean excessKnown, boolean termAccepted, boolean homeUnOccupied) {
        JSONObject claimQuestionCommon = new JSONObject();
        claimQuestionCommon.put("mitigatingAction_Ins", mitigationAction)
                .put("excessKnown_Ins", excessKnown)
                .put("termAccepted_Ins", termAccepted);
        claimQuestionCommon.put("unoccupied_Ins", homeUnOccupied);
        if (homeUnOccupied) {
            claimQuestionCommon.put("lastOccupied_Ins", new JSONObject()
                    .put("year", 2016)
                    .put("month", 3)
                    .put("day", 12));
        }

        return claimQuestionCommon;
    }

    public JSONObject getClaimQuestionCommonAD(String mitigationAction, boolean excessKnown, boolean termAccepted, boolean homeUnOccupied, String[] detailsAD) {
        JSONObject claimQuestionCommon = new JSONObject();
        claimQuestionCommon.put("mitigatingAction_Ins", mitigationAction)
                .put("excessKnown_Ins", excessKnown)
                .put("termAccepted_Ins", termAccepted);
        if (detailsAD[0].contains("home")) {
            claimQuestionCommon.put("whereDidThisHappen", "home");
            claimQuestionCommon.put("unoccupied_Ins", homeUnOccupied);
            if (Arrays.asList(detailsAD).contains("withinHome")) {
                claimQuestionCommon.put("lossHappenedWithinHome", true);
            }
            if (Arrays.asList(detailsAD).contains("outside")) {
                claimQuestionCommon.put("lossHappenedOutside", true);
            }
            if (Arrays.asList(detailsAD).contains("outbuilding")) {
                claimQuestionCommon.put("lossHappenedOutbuilding", true);
            }
            if (homeUnOccupied) {
                claimQuestionCommon.put("lastOccupied_Ins", new JSONObject()
                        .put("year", 2016)
                        .put("month", 3)
                        .put("day", 12));
            }
        } else if (detailsAD[0].contains("withinUK")) {
            claimQuestionCommon.put("whereDidThisHappen", "withinUK");
            claimQuestionCommon.put("whereWasIt", detailsAD[1]);
        } else if (detailsAD[0].equalsIgnoreCase("outsideUK")) {
            claimQuestionCommon.put("whereDidThisHappen", "outsideUK");
            claimQuestionCommon.put("nbrOfDaysItemAbroad", detailsAD[1]);
            claimQuestionCommon.put("whereWasIt", detailsAD[2]);
        }

        return claimQuestionCommon;
    }


    public JSONObject getClaimQuestionFreezer(int lossAmount, int freezerAge, String maintenanceContract) {
        JSONObject claimQuestionFreezer = new JSONObject();
        claimQuestionFreezer.put("lossValueAmount_Ins", lossAmount)
                .put("freezerAge_Ins", freezerAge)
                .put("claimTempFreezer_Ins", false);

        if (freezerAge > 10) {
            claimQuestionFreezer.put("maintenanceContract_Ins", maintenanceContract);
        }
        return claimQuestionFreezer;
    }

    public void createJsonFF(boolean mainPolicyHolder, int freezerAge, String maintenanceContract, String mitigationAction, boolean excessKnown, boolean termAccepted, boolean homeUnOccupied) {
        try {
            String jsonString = new JSONObject()
                    .put("id", "12")
                    .put("method", "createClaim")
                    .put("params", new JSONArray().put("aportaluser")
                            .put(completeDetailsFF(mainPolicyHolder, freezerAge, maintenanceContract, mitigationAction, excessKnown, termAccepted, homeUnOccupied)))
                    .put("jsonrpc", "2.0")
                    .toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject completeDetailsFF(boolean mainPolicyHolder, int freezerAge, String maintenanceContract, String mitigationAction, boolean excessKnown, boolean termAccepted, boolean homeUnoccupied) {
        JSONObject otherDetails = new JSONObject()
                .put("lossDate", new JSONObject()
                        .put("year", 2016)
                        .put("month", 12)
                        .put("day", 20))
                .put("lossType", "PR")
                .put("lossCause", "frozenfood_Ins")
                .put("description", "what happened desc")
                .put("mainContact", getMainContact(mainPolicyHolder))
                .put("contacts", new JSONArray())
                .put("lossLocation", new JSONObject()
                        .put("displayName", "")
                        .put("country", "GB"))
                .put("relatedContacts", getRelatedContact(mainPolicyHolder))
                .put("lobs", new JSONObject()
                        .put("homeowners", new JSONObject()))
                .put("documents", new JSONArray())
                .put("claimQuestionCommon_Ins", getClaimQuestionCommonFF(mitigationAction, excessKnown, termAccepted, homeUnoccupied))
                .put("claimQuestionFreezer_Ins", getClaimQuestionFreezer(23, freezerAge, maintenanceContract))
                .put("hasDeclaredClaims_Ins", false)
                .put("hasHomeConviction_Ins", false)
                .put("declaredClaims_Ins", new JSONArray())
                .put("homeConvictionInfo_Ins", new JSONArray())
                .put("selectedCallbackSlot_Ins", "2016-12-20 9am - 1pm")
                .put("lossLocationPostCode", "br123dd")
                .put("lossLocationAddress", "test")
                .put("submittedToCC5", false)
                .put("isMainContactPolicyHolder_Ins", mainPolicyHolder);

        return otherDetails;
    }

    public JSONObject completeDetailsAD(boolean mainPolicyHolder, int freezerAge, String maintenanceContract, String mitigationAction, boolean excessKnown, boolean termAccepted, boolean homeUnoccupied, String[] details) {
        JSONObject otherDetails = new JSONObject()
                .put("lossDate", new JSONObject()
                        .put("year", 2016)
                        .put("month", 12)
                        .put("day", 20))
                .put("lossType", "PR")
                .put("lossCause", "accidentaldmg_Ins")
                .put("description", "what happened desc")
                .put("mainContact", getMainContact(mainPolicyHolder))
                .put("contacts", new JSONArray())
                .put("lossLocation", new JSONObject()
                        .put("displayName", "")
                        .put("country", "GB"))
                .put("relatedContacts", getRelatedContact(mainPolicyHolder))
                .put("lobs", new JSONObject()
                        .put("homeowners", new JSONObject()))
                .put("documents", new JSONArray())
                .put("claimQuestionCommon_Ins", getClaimQuestionCommonAD(mitigationAction, excessKnown, termAccepted, homeUnoccupied, details))
                .put("hasDeclaredClaims_Ins", false)
                .put("hasHomeConviction_Ins", false)
                .put("declaredClaims_Ins", new JSONArray())
                .put("homeConvictionInfo_Ins", new JSONArray())
                .put("selectedCallbackSlot_Ins", "2016-12-20 9am - 1pm")
                .put("lossLocationPostCode", "br123dd")
                .put("lossLocationAddress", "test")
                .put("submittedToCC5", false)
                .put("isMainContactPolicyHolder_Ins", mainPolicyHolder);

        return otherDetails;
    }


    public String contactDetailsRequest(String peril, String firstName, String lastName, String contactNumber, String email) {
        String jsonString = "";
        try {
            jsonString = new JSONObject()
                    .put("id", "1")
                    .put("method", "createClaim")
                    .put("params", new JSONArray().put("aportaluser")
                            .put(new JSONObject().put("lossDate", "2016-12-15T18:00:00.000Z")
                                    .put("contacts", new JSONArray()
                                            .put(new JSONObject()
                                                    .put("tempID", 1)
                                                    .put("subtype", "Person")
                                                    .put("firstName", firstName)
                                                    .put("lastName", lastName)
                                                    .put("homeNumber", "01111111111")
                                                    .put("emailAddress1", "asd@asd.com")))
                                    .put("relatedContacts", new JSONArray())
                                    .put("lobs", new JSONObject())
                                    .put("lossType", "PR")
                                    .put("lossCause", peril)
                                    .put("policy", new JSONObject()
                                            .put("policyNumber", "aportaluser")
                                            .put("policyType", "Homeowners"))
                                    .put("controlStage_Ins", "1")
                                    .put("isMainContactPolicyHolder_Ins", true)
                                    .put("mainContact", new JSONObject()
                                            .put("tempID", 1)
                                            .put("subtype", "Person")
                                            .put("firstName", firstName)
                                            .put("lastName", lastName)
                                            .put("homeNumber", contactNumber)
                                            .put("emailAddress1", email))))
                    .put("jsonrpc", "2.0")
                    .toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;

    }

    public String submitClaimRequest(String peril, String firstName, String lastName, String contactNumber, String email) {
        String jsonString = "";
        try {
            jsonString = new JSONObject()
                    .put("id", "1")
                    .put("method", "createClaim")
                    .put("params", new JSONArray().put("aportaluser")
                            .put(new JSONObject().put("lossDate", "2016-12-15T18:00:00.000Z")
                                    .put("contacts", new JSONArray()
                                            .put(new JSONObject()
                                                    .put("tempID", 1)
                                                    .put("subtype", "Person")
                                                    .put("firstName", firstName)
                                                    .put("lastName", lastName)
                                                    .put("homeNumber", "01111111111")
                                                    .put("emailAddress1", "asd@asd.com")))
                                    .put("relatedContacts", new JSONArray())
                                    .put("lobs", new JSONObject())
                                    .put("lossType", "PR")
                                    .put("lossCause", peril)
                                    .put("policy", new JSONObject()
                                            .put("policyNumber", "aportaluser")
                                            .put("policyType", "Homeowners"))
                                    .put("controlStage_Ins", "1")
                                    .put("isMainContactPolicyHolder_Ins", true)
                                    .put("mainContact", new JSONObject()
                                            .put("tempID", 1)
                                            .put("subtype", "Person")
                                            .put("firstName", firstName)
                                            .put("lastName", lastName)
                                            .put("homeNumber", contactNumber)
                                            .put("emailAddress1", email))))
                    .put("jsonrpc", "2.0")
                    .toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;

    }

    public String getResponseFromFNOL(String jSonRequest) {
        HttpResponse response = null;
        String result = "";
        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build();
            HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            HttpPost request = new HttpPost(System.getProperty("ENV") + ".claimportal.scor.dlg/claim/service/edge/fnol/fnol");
            StringEntity params = new StringEntity(jSonRequest);
            request.addHeader("content-type", "application/json; charset=UTF-8");
            request.addHeader("Authorization", "Basic dXBzdHJlYW06cGFzc3dvcmQ=");
            request.addHeader("UserToken", "00525000001uuBg");
            request.setEntity(params);
            response = httpClient.execute(request);
            result = EntityUtils.toString(response.getEntity());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public String getResponse(String jSonRequest) {
        HttpResponse response = null;
        String result = "";
        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build();
            HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            HttpPost request = new HttpPost(System.getProperty("ENV") + ".claimportal.scor.dlg/claim/service/edge/test/selenium");
            StringEntity params = new StringEntity(jSonRequest);
            request.addHeader("content-type", "application/json; charset=UTF-8");
            request.addHeader("Authorization", "Basic dXBzdHJlYW06cGFzc3dvcmQ=");
            request.addHeader("UserToken", "00525000001uuBg");
            request.setEntity(params);
            response = httpClient.execute(request);
            result = EntityUtils.toString(response.getEntity());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }


    public String getTimeSlotAvailability(String jSonRequest) {
        HttpResponse response = null;
        String result = "";
        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build();
            HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            HttpPost request = new HttpPost(System.getProperty("ENV") + ".claimportal.scor.dlg/claim/service/edge/fnol/fnol");
            StringEntity params = new StringEntity(jSonRequest);
            request.addHeader("content-type", "application/json; charset=UTF-8");
            request.addHeader("Authorization", "Basic dXBzdHJlYW06cGFzc3dvcmQ=");
            request.addHeader("UserToken", "00525000001uuBg");
            request.setEntity(params);
            response = httpClient.execute(request);
            result = EntityUtils.toString(response.getEntity());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public String setPerilAvailability(String availability) {
        HttpResponse response = null;
        String result = "";
        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build();
            HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            File f = new File("src/test/resources/Requests/SetPerilAvailabilityAllActive.txt");
            String jSonRequest = new String(Files.readAllBytes(Paths.get(f.getPath())));
            jSonRequest = jSonRequest.replace("[true, true, true, true, true]", availability);
            HttpPost request = new HttpPost(System.getProperty("ENV") + ".claimportal.scor.dlg/claim/service/edge/test/selenium");
            StringEntity params = new StringEntity(jSonRequest);
            request.addHeader("content-type", "application/json; charset=UTF-8");
            request.addHeader("Authorization", "Basic dXBzdHJlYW06cGFzc3dvcmQ=");
            request.addHeader("UserToken", "00525000001uuBg");
            request.setEntity(params);
            response = httpClient.execute(request);
            result = EntityUtils.toString(response.getEntity());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public String createClaim(String peril, String userId) {

        HttpResponse response = null;
        String result = "";
        try {
            File f = new File("src/test/resources//Requests/CreateClaimRequest-" + peril + ".txt");
            String jSonRequest = new String(Files.readAllBytes(Paths.get(f.getPath())));
            jSonRequest = jSonRequest.replaceAll("<username>", userId);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build();
            HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
            HttpPost request = new HttpPost(System.getProperty("ENV") + ".claimportal.scor.dlg/claim/service/edge/fnol/fnol");
            StringEntity params = new StringEntity(jSonRequest);
            request.addHeader("content-type", "application/json; charset=UTF-8");
            request.addHeader("Authorization", "Basic dXBzdHJlYW06cGFzc3dvcmQ=");
            request.addHeader("UserToken", userId);
            request.setEntity(params);
            response = httpClient.execute(request);
            result = EntityUtils.toString(response.getEntity());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public List<String> getJSonValue(String jSOnResponse, String keyName) {
        JsonReader jsonReader = Json.createReader(new StringReader(jSOnResponse));
        JsonObject jsonObject = jsonReader.readObject();
        value.clear();
        return navigateTree(jsonObject, "", keyName);

    }

    List<String> value = new ArrayList<>();

    public List<String> navigateTree(JsonValue tree, String key, String keyName) {

        if (key != null)
            System.out.print("Key " + key + ": ");
        switch (tree.getValueType()) {
            case OBJECT:
                System.out.println("OBJECT");
                JsonObject object = (JsonObject) tree;
                for (String name : object.keySet()) {
                    navigateTree(object.get(name), name, keyName);
                }
                break;
            case ARRAY:
                System.out.println("ARRAY");
                JsonArray array = (JsonArray) tree;
                if (key.contains(keyName)) {
                    value.add(array.toString());
                }
                for (JsonValue val : array)
                    navigateTree(val, null, keyName);
                break;
            case STRING:
                JsonString st = (JsonString) tree;
                if (key.contains(keyName)) {
                    value.add(st.getString());
                }
                System.out.println("STRING " + st.getString());
                break;
            case NUMBER:
                JsonNumber num = (JsonNumber) tree;
                if (key.contains(keyName)) {
                    value.add(num.toString());
                }
                System.out.println("NUMBER " + num.toString());
                break;
            case TRUE:
            case FALSE:
            case NULL:
                System.out.println(tree.getValueType().toString());
                break;
        }
        return value;
    }

    public String callCreateClaimService(String requestPath) throws Throwable {
        String jSonRequest = new String(Files.readAllBytes(Paths.get(requestPath)));
        String resp = "";
        if (requestPath.contains("SetPerilAvailability"))
            resp = getResponse(jSonRequest);
        else
            resp = getResponseFromFNOL(jSonRequest);
        return resp;
    }

    public String SOAPResponse(String soapRequest) {
        String result = "";
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost("http://claimcenterproxy.scor.dlg/cc/soap/IClaimAPI");
            //String jSonRequest = new String(Files.readAllBytes(Paths.get(filePath)));
            System.out.println("request ==============  " + soapRequest);
            HttpEntity entity = new ByteArrayEntity(soapRequest.getBytes("UTF-8"));
            request.setEntity(entity);
            request.addHeader("SOAPAction", "");
            HttpResponse response = httpClient.execute(request);
            System.out.println("ssssssssstttttttttttttttaaaaaaaaaaaaaaaaaaatttttttttttttttttuuuuuuuuuuuusssssssss" + response.getStatusLine());
            result = EntityUtils.toString(response.getEntity());
            System.out.println("result ====================== " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getNodeValue(String xml, String node) {
        String value = "";
        Document doc = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));
            doc = builder.parse(src);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        value = doc.getElementsByTagName(node).item(0).getTextContent();
        return value;
    }


}
