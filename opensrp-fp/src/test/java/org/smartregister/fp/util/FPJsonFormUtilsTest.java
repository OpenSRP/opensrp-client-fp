package org.smartregister.fp.util;

import android.graphics.Bitmap;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.fp.R;
import org.smartregister.fp.common.library.FPLibrary;
import org.smartregister.fp.common.model.ContactSummaryModel;
import org.smartregister.fp.common.util.ConstantsUtils;
import org.smartregister.fp.common.util.DBConstantsUtils;
import org.smartregister.fp.common.util.FPJsonFormUtils;
import org.smartregister.fp.common.util.FileUtil;
import org.smartregister.fp.common.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.ImageRepository;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

/**
 * Created by ndegwamartin on 13/11/2018.
 */

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({
        "org.powermock.*",
        "org.mockito.*",
})
@PrepareForTest(LocationHelper.class)
public class FPJsonFormUtilsTest {
    private static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";
    private static final String DUMMY_LOCATION_ID = "dummy-location-id-2018";
    private final String registerFormJsonString = "{\r\n\t\"count\": \"1\",\r\n\t\"encounter_type\": \"ANC Registration\"," +
            "\r\n\t\"entity_id\": \"\",\r\n\t\"relational_id\": \"\",\r\n\t\"metadata\": {\r\n\t\t\"start\": " +
            "{\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"concept\"," +
            "\r\n\t\t\t\"openmrs_data_type\": \"start\",\r\n\t\t\t\"openmrs_entity_id\": " +
            "\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"end\": {\r\n\t\t\t\"openmrs_entity_parent\": " +
            "\"\",\r\n\t\t\t\"openmrs_entity\": \"concept\",\r\n\t\t\t\"openmrs_data_type\": \"end\"," +
            "\r\n\t\t\t\"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"today\": " +
            "{\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"encounter\"," +
            "\r\n\t\t\t\"openmrs_entity_id\": \"encounter_date\"\r\n\t\t},\r\n\t\t\"deviceid\": " +
            "{\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"concept\",\r\n\t\t\t\"openmrs_data_type\": \"deviceid\",\r\n\t\t\t\"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"subscriberid\": {\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"concept\",\r\n\t\t\t\"openmrs_data_type\": \"subscriberid\",\r\n\t\t\t\"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"simserial\": {\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"concept\",\r\n\t\t\t\"openmrs_data_type\": \"simserial\",\r\n\t\t\t\"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"phonenumber\": {\r\n\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\"openmrs_entity\": \"concept\",\r\n\t\t\t\"openmrs_data_type\": \"phonenumber\",\r\n\t\t\t\"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\r\n\t\t},\r\n\t\t\"encounter_location\": \"\",\r\n\t\t\"look_up\": {\r\n\t\t\t\"entity_id\": \"\",\r\n\t\t\t\"value\": \"\"\r\n\t\t}\r\n\t},\r\n\t\"step1\": {\r\n\t\t\"title\": \"ANC Registration\",\r\n\t\t\"fields\": [{\r\n\t\t\t\t\"key\": \"wom_image\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"\",\r\n\t\t\t\t\"openmrs_entity_id\": \"\",\r\n\t\t\t\t\"type\": \"choose_image\",\r\n\t\t\t\t\"uploadButtonText\": \"Take a picture of the woman\"\r\n\t\t\t},{\r\n\t\t\t\t\"key\": \"edd\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"\",\r\n\t\t\t\t\"openmrs_entity_id\": \"\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"value\": \"\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"fp_id\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_identifier\",\r\n\t\t\t\t\"openmrs_entity_id\": \"FP_ID\",\r\n\t\t\t\t\"type\": \"barcode\",\r\n\t\t\t\t\"barcode_type\": \"qrcode\",\r\n\t\t\t\t\"hint\": \"ANC ID *\",\r\n\t\t\t\t\"value\": \"0\",\r\n\t\t\t\t\"scanButtonText\": \"Scan QR Code\",\r\n\t\t\t\t\"v_numeric\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter a valid ANC ID\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter the Woman's ANC ID\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"first_name\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person\",\r\n\t\t\t\t\"openmrs_entity_id\": \"first_name\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"First name *\",\r\n\t\t\t\t\"edit_type\": \"name\",\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter the first name\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"[A-Za-z]\",\r\n\t\t\t\t\t\"err\": \"Please enter a valid name\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"last_name\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person\",\r\n\t\t\t\t\"openmrs_entity_id\": \"last_name\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Last name *\",\r\n\t\t\t\t\"edit_type\": \"name\",\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter the last name\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"[A-Za-z]\",\r\n\t\t\t\t\t\"err\": \"Please enter a valid name\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"gender\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person\",\r\n\t\t\t\t\"openmrs_entity_id\": \"gender\",\r\n\t\t\t\t\"type\": \"hidden\",\r\n\t\t\t\t\"value\": \"female\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"dob\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person\",\r\n\t\t\t\t\"openmrs_entity_id\": \"birthdate\",\r\n\t\t\t\t\"type\": \"date_picker\",\r\n\t\t\t\t\"hint\": \"Date of birth (DOB) *\",\r\n\t\t\t\t\"expanded\": false,\r\n\t\t\t\t\"duration\": {\r\n\t\t\t\t\t\"label\": \"Age\"\r\n\t\t\t\t},\r\n\t\t\t\t\"min_date\": \"today-49y\",\r\n\t\t\t\t\"max_date\": \"today-15y\",\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter the date of birth\"\r\n\t\t\t\t},\r\n\t\t\t\t\"relevance\": {\r\n\t\t\t\t\t\"step1:dob_unknown\": {\r\n\t\t\t\t\t\t\"ex-checkbox\": [{\r\n\t\t\t\t\t\t\t\"not\": [\r\n\t\t\t\t\t\t\t\t\"dob_unknown\"\r\n\t\t\t\t\t\t\t]\r\n\t\t\t\t\t\t}]\r\n\t\t\t\t\t}\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"dob_unknown\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person\",\r\n\t\t\t\t\"openmrs_entity_id\": \"birthdateApprox\",\r\n\t\t\t\t\"type\": \"check_box\",\r\n\t\t\t\t\"label\": \"\",\r\n\t\t\t\t\"options\": [{\r\n\t\t\t\t\t\"key\": \"dob_unknown\",\r\n\t\t\t\t\t\"text\": \"DOB unknown?\",\r\n\t\t\t\t\t\"text_size\": \"18px\",\r\n\t\t\t\t\t\"value\": \"false\"\r\n\t\t\t\t}]\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"age\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"age\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Age *\",\r\n\t\t\t\t\"v_numeric\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Number must begin with 0 and must be a total of 10 digits in length\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_min\": {\r\n\t\t\t\t\t\"value\": \"15\",\r\n\t\t\t\t\t\"err\": \"Age must be equal or greater than 15\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_max\": {\r\n\t\t\t\t\t\"value\": \"49\",\r\n\t\t\t\t\t\"err\": \"Age must be equal or less than 49\"\r\n\t\t\t\t},\r\n\t\t\t\t\"relevance\": {\r\n\t\t\t\t\t\"step1:dob_unknown\": {\r\n\t\t\t\t\t\t\"ex-checkbox\": [{\r\n\t\t\t\t\t\t\t\"and\": [\r\n\t\t\t\t\t\t\t\t\"dob_unknown\"\r\n\t\t\t\t\t\t\t]\r\n\t\t\t\t\t\t}]\r\n\t\t\t\t\t}\r\n\t\t\t\t},\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": true,\r\n\t\t\t\t\t\"err\": \"Please enter the woman's age\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"home_address\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"home_address\",\r\n\t\t\t\t\"openmrs_data_type\": \"text\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Home address *\",\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Please enter the woman's home address\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"[A-Za-z0-9]\",\r\n\t\t\t\t\t\"err\": \"Please enter a valid name\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"phone_number\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"phone_number\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Mobile phone number *\",\r\n\t\t\t\t\"v_numeric\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Number must be a total of 10 digits in length\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"([0-9]{10})\",\r\n\t\t\t\t\t\"err\": \"Number must be a total of 10 digits in length\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": true,\r\n\t\t\t\t\t\"err\": \"Please specify the woman's phone number\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"reminders\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"reminders\",\r\n\t\t\t\t\"openmrs_data_type\": \"select one\",\r\n\t\t\t\t\"type\": \"spinner\",\r\n\t\t\t\t\"label_info_text\": \"Does she want to receive reminders for care and messages regarding her health throughout her pregnancy?\",\r\n\t\t\t\t\"hint\": \"Reminders throughout pregnancy? *\",\r\n\t\t\t\t\"values\": [\r\n\t\t\t\t\t\"Yes\",\r\n\t\t\t\t\t\"No\"\r\n\t\t\t\t],\r\n\t\t\t\t\"v_required\": {\r\n\t\t\t\t\t\"value\": true,\r\n\t\t\t\t\t\"err\": \"Please select whether the woman has agreed to receiving reminder notifications\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"alt_name\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"alt_name\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Alternate contact name\",\r\n\t\t\t\t\"edit_type\": \"name\",\r\n\t\t\t\t\"look_up\": \"true\",\r\n\t\t\t\t\"entity_id\": \"\",\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"[A-Za-z]\",\r\n\t\t\t\t\t\"err\": \"Please enter a valid VHT name\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"alt_phone_number\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"alt_phone_number\",\r\n\t\t\t\t\"type\": \"edit_text\",\r\n\t\t\t\t\"hint\": \"Alternate contact phone number\",\r\n\t\t\t\t\"v_numeric\": {\r\n\t\t\t\t\t\"value\": \"true\",\r\n\t\t\t\t\t\"err\": \"Number must be a total of 10 digits in length\"\r\n\t\t\t\t},\r\n\t\t\t\t\"v_regex\": {\r\n\t\t\t\t\t\"value\": \"([0-9]{10})\",\r\n\t\t\t\t\t\"err\": \"Number must be a total of 10 digits in length\"\r\n\t\t\t\t}\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"next_contact\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"next_contact\",\r\n\t\t\t\t\"type\": \"hidden\",\r\n\t\t\t\t\"value\": \"\"\r\n\t\t\t},\r\n\t\t\t{\r\n\t\t\t\t\"key\": \"next_contact_date\",\r\n\t\t\t\t\"openmrs_entity_parent\": \"\",\r\n\t\t\t\t\"openmrs_entity\": \"person_attribute\",\r\n\t\t\t\t\"openmrs_entity_id\": \"next_contact_date\",\r\n\t\t\t\t\"type\": \"hidden\",\r\n\t\t\t\t\"value\": \"\"\r\n\t\t\t}\r\n\t\t]\r\n\t}\r\n}";
    private final String expectedProcessedJson = "{\"metadata\":{\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}," +
            "\"encounter_location\":\"\",\"today\":{\"openmrs_entity\":\"encounter\"," +
            "\"openmrs_entity_id\":\"encounter_date\",\"openmrs_entity_parent\":\"\"}," +
            "\"start\":{\"openmrs_data_type\":\"start\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}," +
            "\"phonenumber\":{\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}," +
            "\"subscriberid\":{\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}," +
            "\"simserial\":{\"openmrs_data_type\":\"simserial\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}," +
            "\"end\":{\"openmrs_data_type\":\"end\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}," +
            "\"deviceid\":{\"openmrs_data_type\":\"deviceid\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"}}," +
            "\"count\":\"1\",\"entity_id\":\"00ts-ime-hcla-0tib-0eht-ma0i\",\"relational_id\":\"\",\"encounter_type\":\"Update ANC Registration\",\"step1\":{\"title\":\"ANC Registration\",\"fields\":[{\"uploadButtonText\":\"Take a picture of the woman\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_entity_parent\":\"\",\"type\":\"choose_image\",\"value\":\"\\/images\\/00ts-ime-hcla-0tib-0eht-ma0i\",\"key\":\"wom_image\"},{\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"value\":\"2018-10-19\",\"key\":\"edd\"},{\"openmrs_entity\":\"person_identifier\",\"hint\":\"ANC ID *\",\"openmrs_entity_id\":\"FP_ID\",\"v_required\":{\"err\":\"Please enter the Woman's ANC ID\",\"value\":\"true\"},\"barcode_type\":\"qrcode\",\"openmrs_entity_parent\":\"\",\"v_numeric\":{\"err\":\"Please enter a valid ANC ID\",\"value\":\"true\"},\"type\":\"barcode\",\"value\":\"00tsimehcla0tib0ehtma0i\",\"key\":\"fp_id\",\"scanButtonText\":\"Scan QR Code\"},{\"openmrs_entity\":\"person\",\"read_only\":false,\"hint\":\"First name *\",\"openmrs_entity_id\":\"first_name\",\"edit_type\":\"name\",\"v_required\":{\"err\":\"Please enter the first name\",\"value\":\"true\"},\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"value\":\"First Name\",\"key\":\"first_name\",\"v_regex\":{\"err\":\"Please enter a valid name\",\"value\":\"[A-Za-z]\"}},{\"openmrs_entity\":\"person\",\"read_only\":false,\"hint\":\"Last name *\",\"openmrs_entity_id\":\"last_name\",\"edit_type\":\"name\",\"v_required\":{\"err\":\"Please enter the last name\",\"value\":\"true\"},\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"value\":\"Last Name\",\"key\":\"last_name\",\"v_regex\":{\"err\":\"Please enter a valid name\",\"value\":\"[A-Za-z]\"}},{\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"openmrs_entity_parent\":\"\",\"type\":\"hidden\",\"value\":\"female\",\"key\":\"gender\"},{\"max_date\":\"today-15y\",\"openmrs_entity_id\":\"birthdate\",\"openmrs_entity_parent\":\"\",\"type\":\"date_picker\",\"relevance\":{\"step1:dob_unknown\":{\"ex-checkbox\":[{\"not\":[\"dob_unknown\"]}]}},\"duration\":{\"label\":\"Age\"},\"expanded\":false,\"openmrs_entity\":\"person\",\"min_date\":\"today-49y\",\"hint\":\"Date of birth (DOB) *\",\"v_required\":{\"err\":\"Please enter the date of birth\",\"value\":\"true\"},\"value\":\"09-08-1995\",\"key\":\"dob\"},{\"openmrs_entity\":\"person\",\"read_only\":false,\"openmrs_entity_id\":\"birthdateApprox\",\"options\":[{\"text_size\":\"18px\",\"text\":\"DOB unknown?\",\"value\":\"false\",\"key\":\"dob_unknown\"}],\"openmrs_entity_parent\":\"\",\"label\":\"\",\"type\":\"check_box\",\"key\":\"dob_unknown\"},{\"v_min\":{\"err\":\"Age must be equal or greater than 15\",\"value\":\"15\"},\"openmrs_entity_id\":\"age\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"relevance\":{\"step1:dob_unknown\":{\"ex-checkbox\":[{\"and\":[\"dob_unknown\"]}]}},\"v_max\":{\"err\":\"Age must be equal or less than 49\",\"value\":\"49\"},\"openmrs_entity\":\"person_attribute\",\"read_only\":false,\"hint\":\"Age *\",\"v_required\":{\"err\":\"Please enter the woman's age\",\"value\":true},\"v_numeric\":{\"err\":\"Number must begin with 0 and must be a total of 10 digits in length\",\"value\":\"true\"},\"value\":23" +
            ",\"key\":\"age\"},{\"openmrs_data_type\":\"text\",\"openmrs_entity\":\"person_attribute\",\"hint\":\"Home address *\",\"openmrs_entity_id\":\"home_address\",\"v_required\":{\"err\":\"Please enter the woman's home address\",\"value\":\"true\"},\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"value\":\"Roysambu\",\"key\":\"home_address\",\"v_regex\":{\"err\":\"Please enter a valid name\",\"value\":\"[A-Za-z0-9]\"}},{\"openmrs_entity\":\"person_attribute\",\"hint\":\"Mobile phone number *\",\"openmrs_entity_id\":\"phone_number\",\"v_required\":{\"err\":\"Please specify the woman's phone number\",\"value\":true},\"openmrs_entity_parent\":\"\",\"v_numeric\":{\"err\":\"Number must be a total of 10 digits in length\",\"value\":\"true\"},\"type\":\"edit_text\",\"key\":\"phone_number\",\"v_regex\":{\"err\":\"Number must be a total of 10 digits in length\",\"value\":\"([0-9]{10})\"}},{\"openmrs_data_type\":\"select one\",\"openmrs_entity\":\"person_attribute\",\"hint\":\"Reminders throughout pregnancy? *\",\"values\":[\"Yes\",\"No\"],\"openmrs_entity_id\":\"reminders\",\"v_required\":{\"err\":\"Please select whether the woman has agreed to receiving reminder notifications\",\"value\":true},\"openmrs_entity_parent\":\"\",\"type\":\"spinner\",\"label_info_text\":\"Does she want to receive reminders for care and messages regarding her health throughout her pregnancy?\",\"key\":\"reminders\"},{\"look_up\":\"true\",\"openmrs_entity\":\"person_attribute\",\"hint\":\"Alternate contact name\",\"openmrs_entity_id\":\"alt_name\",\"edit_type\":\"name\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"entity_id\":\"\",\"key\":\"alt_name\",\"v_regex\":{\"err\":\"Please enter a valid VHT name\",\"value\":\"[A-Za-z]\"}},{\"openmrs_entity\":\"person_attribute\",\"hint\":\"Alternate contact phone number\",\"openmrs_entity_id\":\"alt_phone_number\",\"openmrs_entity_parent\":\"\",\"v_numeric\":{\"err\":\"Number must be a total of 10 digits in length\",\"value\":\"true\"},\"type\":\"edit_text\",\"key\":\"alt_phone_number\",\"v_regex\":{\"err\":\"Number must be a total of 10 digits in length\",\"value\":\"([0-9]{10})\"}},{\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"next_contact\",\"openmrs_entity_parent\":\"\",\"type\":\"hidden\",\"value\":\"\",\"key\":\"next_contact\"},{\"openmrs_entity\":\"person_attribute\",\"read_only\":false,\"openmrs_entity_id\":\"next_contact_date\",\"openmrs_entity_parent\":\"\",\"type\":\"hidden\",\"value\":\"2018-08-09\",\"key\":\"next_contact_date\"}]}}";
    private String siteCharacteristics = "{\"count\":\"1\",\"display_scroll_bars\":true,\"encounter_type\":\"Site Characteristics\",\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Site Characteristics\",\"fields\":[{\"key\":\"label_site_ipv_assess\",\"type\":\"label\",\"text\":\"1. Are all of the following in place at your facility:\",\"text_size\":\"22px\",\"text_color\":\"black\",\"v_required\":{\"value\":true,\"err\":\"Please select where stock was issued\"}},{\"key\":\"site_ipv_assess\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"select one\",\"label\":\"a. A protocol or standard operating procedure for Intimate Partner Violence (IPV)<br><br>b. A health worker trained on how to ask about IPV and how to provide the minimum response or beyond <br><br>c. A private setting<br> <br>d. A way to ensure confidentiality<br><br>e. Time to allow for appropriate disclosure<br> <br>f. A system for referral in place.\",\"type\":\"native_radio\",\"options\":[{\"key\":\"1\",\"text\":\"Yes\"},{\"key\":\"0\",\"text\":\"No\"}],\"value\":\"\",\"v_required\":{\"value\":\"false\",\"err\":\"Please select where stock was issued\"}},{\"key\":\"site_anc_hiv\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"select one\",\"label\":\"2. Is the HIV prevalence consistently greater than 1% in pregnant women attending antenatal clinics at your facility?\",\"type\":\"native_radio\",\"options\":[{\"key\":\"1\",\"text\":\"Yes\"},{\"key\":\"0\",\"text\":\"No\"}],\"value\":\"\",\"v_required\":{\"value\":\"true\",\"err\":\"Please select where stock was issued\"}},{\"key\":\"site_ultrasound\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"select one\",\"type\":\"native_radio\",\"label\":\"3. Is an ultrasound machine available and functional at your facility and a trained health worker available to use it?\",\"options\":[{\"key\":\"1\",\"text\":\"Yes\"},{\"key\":\"0\",\"text\":\"No\"}],\"value\":\"\",\"v_required\":{\"value\":\"true\",\"err\":\"Please select where stock was issued\"}},{\"key\":\"site_bp_tool\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"select one\",\"type\":\"native_radio\",\"label\":\"4. Does your facility use an automated blood pressure (BP) measurement tool?\",\"options\":[{\"key\":\"1\",\"text\":\"Yes\"},{\"key\":\"0\",\"text\":\"No\"}],\"value\":\"\",\"v_required\":{\"value\":\"true\",\"err\":\"Please select where stock was issued\"}}]}}";
    private String closeAnc = "{\"count\":\"1\",\"encounter_type\":\"Close FP Record\",\"entity_id\":\"11f0463d-e4a3-42b6-a99b-fd3fd898799f\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-03-27 00:11:58\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-03-27 00:12:23\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"27-03-2020\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Close FP Record\",\"fields\":[{\"key\":\"anc_close_reason\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165245AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Reason?\",\"values\":[\"Live birth\",\"Stillbirth\",\"Miscarriage\",\"Abortion\",\"Woman died\",\"Moved away\",\"False pregnancy\",\"Lost to follow-up\",\"Wrong entry\",\"Other\"],\"openmrs_choice_ids\":{\"Live birth\":\"151849AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Stillbirth\":\"125872AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Miscarriage\":\"48AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Abortion\":\"50AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Woman Died\":\"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Lost to follow-up\":\"5240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Moved away\":\"160415AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"False Pregnancy\":\"128299AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Wrong entry\":\"165246AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Other\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please select one option\"},\"step\":\"step1\",\"is-rule-check\":true,\"value\":\"Live birth\"},{\"key\":\"delivery_date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5599AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"date_picker\",\"hint\":\"Delivery date\",\"expanded\":false,\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of delivery\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_relevance_rules.yml\"}}},\"is_visible\":true,\"value\":\"27-03-2020\"},{\"key\":\"delivery_place\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Place of delivery?\",\"values\":[\"Health facility\",\"Home\",\"Other\"],\"openmrs_choice_ids\":{\"Health facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Other\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":\"true\",\"err\":\"Place of delivery is required\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_relevance_rules.yml\"}}},\"is_visible\":true,\"value\":\"Home\"},{\"key\":\"preterm\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"129218AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hidden\":true,\"v_numeric\":{\"value\":\"true\",\"err\":\"Number must be a number\"},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_calculations_rules.yml\"}}},\"is_visible\":false},{\"key\":\"delivery_mode\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5630AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Delivery mode\",\"values\":[\"Normal\",\"Forceps or Vacuum\",\"C-section\"],\"openmrs_choice_ids\":{\"Normal\":\"1170AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Forceps or Vacuum\":\"118159AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"C-section\":\"1171AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":\"false\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_relevance_rules.yml\"}}},\"is_visible\":true,\"value\":\"Forceps or Vacuum\"},{\"key\":\"birthweight\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Birth weight (kg)\",\"v_required\":{\"value\":false,\"err\":\"Please enter the child's weight at birth\"},\"v_min\":{\"value\":\"0.1\",\"err\":\"Birth weight must be greater than 0\"},\"v_max\":{\"value\":\"10\",\"err\":\"Birth weight must be less than or equal to 10\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid weight between 1 and 10\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_relevance_rules.yml\"}}},\"is_visible\":true,\"value\":\"5\"},{\"key\":\"exclusive_bf\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5526AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Exclusively breastfeeding?\",\"values\":[\"Yes\",\"No\"],\"openmrs_choice_ids\":{\"Yes\":\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"No\":\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":\"false\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_relevance_rules.yml\"}}},\"is_visible\":true,\"value\":\"Yes\"},{\"key\":\"ppfp_method\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"374AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Postpartum FP method?\",\"values\":[\"None\",\"Exclusive breastfeeding\",\"OCP\",\"Condom\",\"Female sterilization\",\"Male sterilization\",\"IUD\",\"Abstinence\",\"Other\"],\"openmrs_choice_ids\":{\"None\":\"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Exclusive breastfeeding\":\"5526AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"OCP\":\"780AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Condom\":\"190AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Female sterlization\":\"5276AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Male sterlization\":\"1489AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"IUD\":\"136452AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Abstinence\":\"159524AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Forceps or Vacuum\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":\"false\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_relevance_rules.yml\"}}},\"is_visible\":true,\"value\":\"OCP\"},{\"key\":\"delivery_complications\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1576AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"check_box\",\"label\":\"Any delivery complications?\",\"hint\":\"Any delivery complications?\",\"label_text_style\":\"bold\",\"exclusive\":[\"None\"],\"options\":[{\"key\":\"None\",\"text\":\"None\",\"value\":true,\"openmrs_choice_id\":\"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Postpartum haemorrhage\",\"text\":\"Postpartum haemorrhage\",\"value\":false,\"openmrs_choice_id\":\"230AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Antepartum haemorrhage\",\"text\":\"Antepartum haemorrhage\",\"value\":false,\"openmrs_choice_id\":\"228AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Placenta praevia\",\"text\":\"Placenta praevia\",\"value\":false,\"openmrs_choice_id\":\"114127AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Placental abruption\",\"text\":\"Placental abruption\",\"value\":false,\"openmrs_choice_id\":\"130108AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Cord prolapse\",\"text\":\"Cord prolapse\",\"value\":false,\"openmrs_choice_id\":\"128420AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Obstructed labour\",\"text\":\"Obstructed labour\",\"value\":false,\"openmrs_choice_id\":\"141596AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Abnormal presentation\",\"text\":\"Abnormal presentation\",\"value\":false,\"openmrs_choice_id\":\"150862AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Pre-eclampsia\",\"text\":\"Pre-eclampsia\",\"value\":false,\"openmrs_choice_id\":\"160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Eclampsia\",\"text\":\"Eclampsia\",\"value\":false,\"openmrs_choice_id\":\"129251AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Perineal tear (2nd, 3rd or 4th degree)\",\"text\":\"Perineal tear (2nd, 3rd or 4th degree)\",\"value\":false,\"openmrs_choice_id\":\"165247AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"Other\",\"text\":\"Other\",\"value\":false,\"openmrs_choice_id\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":\"false\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_relevance_rules.yml\"}}},\"is_visible\":true,\"value\":[\"None\"]},{\"key\":\"miscarriage_abortion_date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165248AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"date_picker\",\"hint\":\"Date of miscarriage\\/abortion\",\"expanded\":false,\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of miscarriage\\/abortion\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_relevance_rules.yml\"}}},\"is_visible\":false},{\"key\":\"miscarriage_abortion_ga\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"miscarriage_abortion_ga\",\"type\":\"hidden\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Number must be a number\"},\"v_required\":{\"value\":false},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_calculations_rules.yml\"}}},\"value\":\"0\"},{\"key\":\"death_date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1543AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"date_picker\",\"hint\":\"Date of death\",\"expanded\":false,\"duration\":{\"label\":\"Yrs\"},\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of death\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_relevance_rules.yml\"}}},\"is_visible\":false},{\"key\":\"death_cause\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1599AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Cause of death?\",\"values\":[\"Unknown\",\"Abortion-related complications\",\"Obstructed labour\",\"Pre-eclampsia\",\"Eclampsia\",\"Postpartum haemorrhage\",\"Antepartum haemorrhage \",\"Placental abruption\",\"Infection\",\"Other\"],\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of death\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"anc_close_relevance_rules.yml\"}}},\"openmrs_choice_ids\":{\"Unknown\":\"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Abortion-related complications\":\"122299AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Obstructed labour\":\"141596AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Pre-eclampsia\":\"129251AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Eclampsia\":\"118744AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Postpartum haemorrhage\":\"230AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Antepartum haemorrhage\":\"228AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Placental abruption\":\"130108AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Infection\":\"130AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Other\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"is_visible\":false}]},\"invisible_required_fields\":\"[miscarriage_abortion_date, death_cause, death_date]\",\"details\":{\"appVersionName\":\"1.7.28-SNAPSHOT\",\"formVersion\":\"\"}}";
    private JSONObject formObject;
    @Mock
    private FPLibrary ancLibrary;
    @Mock
    private Compressor compressor;
    @Mock
    private Bitmap bitmap;
    @Mock
    private File file;
    @Mock
    private OutputStream outputStream;
    @Mock
    private Context context;
    @Mock
    private android.content.Context applicationContext;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private FormUtils formUtils;
    @Mock
    private CoreLibrary coreLibrary;
    @Mock
    private LocationPickerView locationPickerView;
    @Mock
    private LocationHelper locationHelper;
    @Mock
    private Photo photo;

    @Before
    public void setUp() throws JSONException {

        MockitoAnnotations.initMocks(this);

        formObject = new JSONObject();
        JSONObject metadataObject = new JSONObject();
        JSONArray fieldsObject = new JSONArray();


        JSONObject fieldObject = new JSONObject();
        fieldObject.put(FPJsonFormUtils.KEY, ConstantsUtils.JsonFormKeyUtils.FP_ID);
        fieldObject.put(FPJsonFormUtils.VALUE, "");
        fieldObject.put(FPJsonFormUtils.OPENMRS_ENTITY, "");
        fieldObject.put(FPJsonFormUtils.OPENMRS_ENTITY_ID, "");
        fieldObject.put(FPJsonFormUtils.OPENMRS_ENTITY_PARENT, "");

        fieldsObject.put(fieldObject);
        JSONObject step1Object = new JSONObject();
        step1Object.put(FPJsonFormUtils.FIELDS, fieldsObject);

        formObject.put(FPJsonFormUtils.METADATA, metadataObject);
        formObject.put(FPJsonFormUtils.STEP1, step1Object);
    }

    @Test
    public void testGetFormAsJsonInjectsCurrentLocationIDinFormCorrectly() throws Exception {
        FPJsonFormUtils.getFormAsJson(formObject, "random-form-name", DUMMY_BASE_ENTITY_ID, DUMMY_LOCATION_ID);

        JSONObject resultObject = FPJsonFormUtils.getFormAsJson(null, ConstantsUtils.JsonFormUtils.FP_REGISTER, DUMMY_BASE_ENTITY_ID, DUMMY_LOCATION_ID);
        Assert.assertNull(resultObject);

        resultObject = FPJsonFormUtils.getFormAsJson(formObject, ConstantsUtils.JsonFormUtils.FP_REGISTER, DUMMY_BASE_ENTITY_ID, DUMMY_LOCATION_ID);

        JSONArray field = FPJsonFormUtils.fields(resultObject);
        FPJsonFormUtils.getFieldJSONObject(field, DBConstantsUtils.KeyUtils.FP_ID);

        Assert.assertNotNull(resultObject);
        Assert.assertEquals(DUMMY_LOCATION_ID, resultObject.getJSONObject(FPJsonFormUtils.METADATA).get(FPJsonFormUtils.ENCOUNTER_LOCATION));
    }

    @Test
    public void testGetFormAsJsonInjectsANCIDInRegisterFormCorrectly() throws Exception {
        JSONObject resultObject = FPJsonFormUtils.getFormAsJson(formObject, ConstantsUtils.JsonFormUtils.FP_REGISTER, DUMMY_BASE_ENTITY_ID, DUMMY_LOCATION_ID);

        JSONArray field = FPJsonFormUtils.fields(resultObject);
        FPJsonFormUtils.getFieldJSONObject(field, ConstantsUtils.JsonFormKeyUtils.FP_ID);

        Assert.assertNotNull(resultObject);
        Assert.assertEquals(DUMMY_BASE_ENTITY_ID.replaceAll("-", ""), FPJsonFormUtils.getFieldJSONObject(field, ConstantsUtils.JsonFormKeyUtils.FP_ID).get(FPJsonFormUtils.VALUE));
    }

    @Test
    public void testGetFormAsJsonInjectsEntityIDinCloseFormCorrectly() throws Exception {
        formObject.put(FPJsonFormUtils.ENTITY_ID, "");
        JSONObject resultObject = FPJsonFormUtils.getFormAsJson(formObject, ConstantsUtils.JsonFormUtils.FP_CLOSE, DUMMY_BASE_ENTITY_ID, DUMMY_LOCATION_ID);

        JSONArray field = FPJsonFormUtils.fields(resultObject);
        FPJsonFormUtils.getFieldJSONObject(field, ConstantsUtils.JsonFormKeyUtils.FP_ID);

        Assert.assertNotNull(resultObject);
        Assert.assertEquals(DUMMY_BASE_ENTITY_ID, resultObject.getString(FPJsonFormUtils.ENTITY_ID));
    }

    @Test
    public void testValidateParametersReturnsCorrectResult() throws Exception {
        String jsonFormObjectString = formObject.toString();
        JSONArray jsonFormObjectFields = FPJsonFormUtils.fields(formObject);

        Triple<Boolean, JSONObject, JSONArray> validatedResult = FPJsonFormUtils.validateParameters(jsonFormObjectString);

        Assert.assertNotNull(validatedResult);
        Assert.assertTrue(validatedResult.getLeft());

        Assert.assertNotNull(validatedResult.getMiddle());
        JSONAssert.assertEquals(formObject, validatedResult.getMiddle(), false);

        Assert.assertNotNull(validatedResult.getRight());
        JSONAssert.assertEquals(jsonFormObjectFields, validatedResult.getRight(), false);

    }

    @Test
    @PrepareForTest({FPLibrary.class, FileUtil.class, DrishtiApplication.class})
    public void testSaveImageInvokesSaveStaticImageToDiskWithCorrectParams() throws Exception {
        String PROVIDER_ID = "dummy-provider-id";

        PowerMockito.mockStatic(FPLibrary.class);
        PowerMockito.when(FPLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getCompressor()).thenReturn(compressor);
        PowerMockito.when(compressor.compressToBitmap(ArgumentMatchers.any(File.class))).thenReturn(bitmap);

        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
        PowerMockito.when(context.imageRepository()).thenReturn(imageRepository);


        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getAppDir()).thenReturn("/images");


        PowerMockito.mockStatic(FileUtil.class);
        PowerMockito.when(FileUtil.createFileFromPath(ArgumentMatchers.anyString())).thenReturn(file);
        PowerMockito.when(file.exists()).thenReturn(true);
        PowerMockito.when(FileUtil.createFileOutputStream(file)).thenReturn(outputStream);

        FPJsonFormUtils.saveImage(PROVIDER_ID, DUMMY_BASE_ENTITY_ID, "filepath/images/folder/location.jpg");

        Mockito.verify(imageRepository).add(ArgumentMatchers.any(ProfileImage.class));
    }

    @Test
    @PrepareForTest({FPLibrary.class, FileUtil.class, DrishtiApplication.class})
    public void testSaveImageInvokesSaveStaticImageToDiskWithCorrectParamsWhereFileExistsISFalse() throws Exception {
        String PROVIDER_ID = "dummy-provider-id";
        PowerMockito.mockStatic(FPLibrary.class);
        PowerMockito.when(FPLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getCompressor()).thenReturn(compressor);
        PowerMockito.when(compressor.compressToBitmap(ArgumentMatchers.any(File.class))).thenReturn(bitmap);

        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
        PowerMockito.when(context.imageRepository()).thenReturn(imageRepository);


        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getAppDir()).thenReturn("/images");


        PowerMockito.mockStatic(FileUtil.class);
        PowerMockito.when(FileUtil.createFileFromPath(ArgumentMatchers.anyString())).thenReturn(file);
        PowerMockito.when(file.exists()).thenReturn(false);
        PowerMockito.when(FileUtil.createFileOutputStream(file)).thenReturn(outputStream);

        FPJsonFormUtils.saveImage(PROVIDER_ID, DUMMY_BASE_ENTITY_ID, "filepath/images/folder/location.jpg");
        Mockito.verify(imageRepository, Mockito.times(0)).add(ArgumentMatchers.any(ProfileImage.class));
    }

    @Test
    @PrepareForTest({FPLibrary.class, FileUtil.class, DrishtiApplication.class})
    public void testSaveImageInvokesSaveStaticImageToDiskWithCorrectParamsWhereProviderIdAndEntityIdIsNull() throws Exception {
        PowerMockito.mockStatic(FPLibrary.class);
        PowerMockito.when(FPLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getCompressor()).thenReturn(compressor);
        PowerMockito.when(compressor.compressToBitmap(ArgumentMatchers.any(File.class))).thenReturn(null);

        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
        PowerMockito.when(context.imageRepository()).thenReturn(imageRepository);


        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getAppDir()).thenReturn("/images");


        PowerMockito.mockStatic(FileUtil.class);
        PowerMockito.when(FileUtil.createFileFromPath(ArgumentMatchers.anyString())).thenReturn(file);
        PowerMockito.when(file.exists()).thenReturn(true);
        PowerMockito.when(FileUtil.createFileOutputStream(file)).thenReturn(outputStream);

        FPJsonFormUtils.saveImage(null, null, "filepath/images/folder/location.jpg");
        Mockito.verify(imageRepository, Mockito.times(0)).add(ArgumentMatchers.any(ProfileImage.class));
    }

    @Test
    @PrepareForTest({FPLibrary.class, FileUtil.class, DrishtiApplication.class})
    public void testSaveImageInvokesSaveStaticImageToDiskWithCorrectParamsWhereFileNotFoundIsThrown() throws Exception {
        String PROVIDER_ID = "dummy-provider-id";
        PowerMockito.mockStatic(FPLibrary.class);
        PowerMockito.when(FPLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getCompressor()).thenReturn(compressor);
        PowerMockito.when(compressor.compressToBitmap(ArgumentMatchers.any(File.class))).thenReturn(bitmap);

        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
        PowerMockito.when(context.imageRepository()).thenReturn(imageRepository);


        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getAppDir()).thenReturn("/images");


        PowerMockito.mockStatic(FileUtil.class);
        PowerMockito.when(FileUtil.createFileFromPath(ArgumentMatchers.anyString())).thenReturn(file);
        PowerMockito.when(file.exists()).thenReturn(true);
        PowerMockito.when(FileUtil.createFileOutputStream(file)).thenThrow(FileNotFoundException.class);

        FPJsonFormUtils.saveImage(PROVIDER_ID, DUMMY_BASE_ENTITY_ID, "filepath/images/folder/location.jpg");
        Mockito.verify(imageRepository, Mockito.times(0)).add(ArgumentMatchers.any(ProfileImage.class));
    }

    @Test
    @PrepareForTest({FPLibrary.class, FileUtil.class, DrishtiApplication.class})
    public void testSaveImageInvokesSaveStaticImageToDiskWithCorrectParamsWhereCompressToBitmapException() throws Exception {
        String PROVIDER_ID = "dummy-provider-id";
        PowerMockito.mockStatic(FPLibrary.class);
        PowerMockito.when(FPLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getCompressor()).thenReturn(compressor);
        PowerMockito.when(compressor.compressToBitmap(ArgumentMatchers.any(File.class))).thenThrow(IOException.class);

        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
        PowerMockito.when(context.imageRepository()).thenReturn(imageRepository);

        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getAppDir()).thenReturn("/images");


        PowerMockito.mockStatic(FileUtil.class);
        PowerMockito.when(FileUtil.createFileFromPath(ArgumentMatchers.anyString())).thenReturn(file);
        PowerMockito.when(file.exists()).thenReturn(true);
        PowerMockito.when(FileUtil.createFileOutputStream(file)).thenThrow(FileNotFoundException.class);
        FPJsonFormUtils.saveImage(PROVIDER_ID, DUMMY_BASE_ENTITY_ID, "filepath/images/folder/location.jpg");
        Mockito.verify(imageRepository, Mockito.times(0)).add(ArgumentMatchers.any(ProfileImage.class));
    }

    @Test
    @PrepareForTest({FormUtils.class, CoreLibrary.class, LocationHelper.class, ImageUtils.class})
    public void testGetAutoPopulatedJsonEditFormStringInjectsValuesCorrectlyInForm() throws Exception {
        String firstName = "First Name";
        String lastName = "Last Name";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KeyUtils.FIRST_NAME, firstName);
        details.put(DBConstantsUtils.KeyUtils.LAST_NAME, lastName);
        details.put(DBConstantsUtils.KeyUtils.EDD, "2018-10-19");
        details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, "2018-08-09");
        details.put(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        details.put(DBConstantsUtils.KeyUtils.FP_ID, DUMMY_BASE_ENTITY_ID);
        details.put(DBConstantsUtils.KeyUtils.DOB, "09-08-1995");
        details.put(DBConstantsUtils.KeyUtils.DOB_UNKNOWN, "false");
        details.put(DBConstantsUtils.KeyUtils.HOME_ADDRESS, "Roysambu");
        details.put(DBConstantsUtils.KeyUtils.AGE, "23");

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.applicationContext()).thenReturn(applicationContext);

        PowerMockito.mock(LocationPickerView.class);
        PowerMockito.whenNew(LocationPickerView.class).withArguments(applicationContext).thenReturn(locationPickerView);

        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.when(FormUtils.getInstance(applicationContext)).thenReturn(formUtils);

        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);

        ArrayList<String> allLevels = new ArrayList<>();
        allLevels.add("Country");
        allLevels.add("Province");
        allLevels.add("District");
        allLevels.add("City/Town");
        allLevels.add("Health Facility");
        allLevels.add(Utils.HOME_ADDRESS);

        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add(Utils.HOME_ADDRESS);

        List<FormLocation> formLocations = new ArrayList<>();
        FormLocation formLocation = new FormLocation();
        formLocation.key = DBConstantsUtils.KeyUtils.HOME_ADDRESS;
        formLocation.name = details.get(DBConstantsUtils.KeyUtils.HOME_ADDRESS);
        formLocations.add(formLocation);


        List<String> locations = new ArrayList<>();
        locations.add(details.get(Utils.HOME_ADDRESS));
        PowerMockito.when(locationHelper.generateDefaultLocationHierarchy(ArgumentMatchers.eq(healthFacilities))).thenReturn(locations);
        PowerMockito.when(locationHelper.generateLocationHierarchyTree(ArgumentMatchers.eq(false), ArgumentMatchers.eq(healthFacilities))).thenReturn(formLocations);
        PowerMockito.when(locationHelper.getOpenMrsLocationId(ArgumentMatchers.anyString())).thenReturn(DUMMY_LOCATION_ID);

        PowerMockito.mockStatic(ImageUtils.class);
        PowerMockito.when(ImageUtils.profilePhotoByClientID(DUMMY_BASE_ENTITY_ID, Utils.getProfileImageResourceIdentifier())).thenReturn(photo);
        PowerMockito.when(photo.getFilePath()).thenReturn("/images/" + DUMMY_BASE_ENTITY_ID);

        JSONObject registerForm = new JSONObject(registerFormJsonString);
        PowerMockito.when(formUtils.getFormJson(ConstantsUtils.JsonFormUtils.FP_REGISTER)).thenReturn(registerForm);

        String resultJsonFormString = FPJsonFormUtils.getAutoPopulatedJsonEditRegisterFormString(applicationContext, details);

        Assert.assertNotNull(resultJsonFormString);
        JSONAssert.assertEquals(expectedProcessedJson, resultJsonFormString, new CustomComparator(JSONCompareMode.LENIENT, new Customization("step1.fields[key=age]", new AgeValueMatcher())));
    }

    @Test
    public void testGetTemplate() {
        String template = "Occupation: {occupation}";
        FPJsonFormUtils ANCJsonFormUtils = new FPJsonFormUtils();
        FPJsonFormUtils.Template actualTemplate = ANCJsonFormUtils.getTemplate(template);
        Assert.assertEquals("Occupation", actualTemplate.title);
    }

    @Test
    public void testGetTemplateWithNoParts() {
        String template = "Occupation Here";
        FPJsonFormUtils ANCJsonFormUtils = new FPJsonFormUtils();
        FPJsonFormUtils.Template actualTemplate = ANCJsonFormUtils.getTemplate(template);
        Assert.assertEquals("Occupation Here", actualTemplate.title);
        Assert.assertEquals("Yes", actualTemplate.detail);
    }

    @Test
    public void testGetTemplateWithOnePart() {
        String template = "Occupation Here:";
        FPJsonFormUtils ANCJsonFormUtils = new FPJsonFormUtils();
        FPJsonFormUtils.Template actualTemplate = ANCJsonFormUtils.getTemplate(template);
        Assert.assertEquals("Occupation Here", actualTemplate.title);
    }

    @Test
    @PrepareForTest({FPLibrary.class, DrishtiApplication.class})
    public void testGenerateNextContactSchedule() {
        String edd = "2020-01-10";
        List<String> contactSchedule = new ArrayList<>();
        contactSchedule.add("31");
        contactSchedule.add("35");
        contactSchedule.add("37");
        contactSchedule.add("39");
        contactSchedule.add("40");
        contactSchedule.add("41");

        PowerMockito.mockStatic(FPLibrary.class);
        PowerMockito.when(FPLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
        PowerMockito.when(context.getStringResource(R.string.contact_number)).thenReturn("Contact %1$d");

        FPJsonFormUtils ANCJsonFormUtils = new FPJsonFormUtils();
        List<ContactSummaryModel> contactSummaryModels = ANCJsonFormUtils.generateNextContactSchedule(edd, contactSchedule, 1);
        Assert.assertEquals("Contact 1", contactSummaryModels.get(0).getContactName());
    }

    @Test
    @PrepareForTest({FPLibrary.class, DrishtiApplication.class})
    public void testGenerateNextContactScheduleWithNullOrEmptyEdd() {
        String edd = "";
        List<String> contactSchedule = new ArrayList<>();
        contactSchedule.add("31");
        contactSchedule.add("35");
        contactSchedule.add("37");
        contactSchedule.add("39");
        contactSchedule.add("40");
        contactSchedule.add("41");

        PowerMockito.mockStatic(FPLibrary.class);
        PowerMockito.when(FPLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
        PowerMockito.when(context.getStringResource(R.string.contact_number)).thenReturn("Contact %1$d");

        FPJsonFormUtils ANCJsonFormUtils = new FPJsonFormUtils();
        List<ContactSummaryModel> contactSummaryModels = ANCJsonFormUtils.generateNextContactSchedule(edd, contactSchedule, 1);
        Assert.assertEquals(0, contactSummaryModels.size());
    }

    @Test
    public void testGetChildLocationIdShouldReturnCorrectValue() {
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
        PowerMockito.when(locationHelper.getOpenMrsLocationId("here")).thenReturn("123-3423");
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        Mockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn("here");
        Assert.assertEquals("123-3423", FPJsonFormUtils.getChildLocationId("234-23", allSharedPreferences));
    }

    @Test
    public void testGetChildLocationIdShouldReturnNullWhenCurrentLocalityIdBlank() {
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        Mockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn("");
        Assert.assertNull(FPJsonFormUtils.getChildLocationId("234-23", allSharedPreferences));
    }

    @Test
    public void testProcessSiteCharacteristics() {
        Map<String, String> settings = FPJsonFormUtils.processSiteCharacteristics(siteCharacteristics);
        Assert.assertNotNull(settings);
        Assert.assertEquals(4, settings.size());
        Assert.assertEquals("0", settings.get("site_bp_tool"));
    }

    @Test
    @PrepareForTest({FormUtils.class})
    public void testGetAutoPopulatedSiteCharacteristicsEditFormString() throws Exception {
        PowerMockito.mockStatic(FormUtils.class);
        PowerMockito.when(FormUtils.getInstance(applicationContext)).thenReturn(formUtils);
        PowerMockito.when(formUtils.getFormJson(ConstantsUtils.JsonFormUtils.ANC_SITE_CHARACTERISTICS)).thenReturn(new JSONObject(siteCharacteristics));

        Map<String, String> settings = FPJsonFormUtils.processSiteCharacteristics(siteCharacteristics);
        Assert.assertNotNull(settings);

        String siteSettings = FPJsonFormUtils.getAutoPopulatedSiteCharacteristicsEditFormString(applicationContext, settings);
        Assert.assertNotNull(siteSettings);
    }

    @Test
    @PrepareForTest({LocationHelper.class, FPLibrary.class})
    public void testSaveRemovedFromANCRegister() {
        String providerId = "demo";

        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        Mockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn("locality");

        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
        PowerMockito.when(locationHelper.getOpenMrsLocationId("locality")).thenReturn("821a7e48-2592-46be-99d6-d29bc4e58839");

        PowerMockito.mockStatic(FPLibrary.class);
        PowerMockito.when(FPLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getDatabaseVersion()).thenReturn(2);

        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(providerId);
        Mockito.when(allSharedPreferences.fetchDefaultLocalityId(providerId)).thenReturn("4fca717e-6072-472e-82f6-bfe3907def66");
        Mockito.when(allSharedPreferences.fetchDefaultTeam(providerId)).thenReturn("bukesa");
        Mockito.when(allSharedPreferences.fetchDefaultTeamId(providerId)).thenReturn("39305854-5db8-4538-a367-8d4b7118f9af");

        Triple<Boolean, Event, Event> eventEventTriple = FPJsonFormUtils.saveRemovedFromFPRegister(allSharedPreferences, closeAnc, providerId);
        Assert.assertNotNull(eventEventTriple);
        Assert.assertFalse(eventEventTriple.getLeft());
        Assert.assertNotNull(eventEventTriple.getMiddle());
        Assert.assertEquals("Close FP Record", eventEventTriple.getMiddle().getEventType());

        Assert.assertNotNull(eventEventTriple.getRight());
        Assert.assertEquals("Update ANC Registration", eventEventTriple.getRight().getEventType());
    }
}
