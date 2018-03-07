package com.avantir.wpos.utils;

import java.util.HashMap;

/**
 * Created by lekanomotayo on 30/01/2018.
 */
public class ConstantUtils {


    public static HashMap<String, String> ISO_CURRENCY_MAP = new HashMap<>();
    static{
        ISO_CURRENCY_MAP.put("566", "NGN");
        ISO_CURRENCY_MAP.put("840", "USD");
        ISO_CURRENCY_MAP.put("826", "GBP");
    }
    public static final int MSG_START_TRANS = 0x2000;
    public static final int READ_CARD = 99;
    public static final int ICC_NEXT = 100;
    public static final int PICC_NEXT = 101;
    public static final int MAG_STRIPE_NEXT = 102;
    public static final int MSG_BACK = 0x1000, MSG_PROGRESS = MSG_BACK + 1,
            MSG_ERROR = MSG_BACK + 2, MSG_RESULT = MSG_BACK + 3, MSG_CARD = MSG_BACK + 4,
            MSG_SWIPE = MSG_BACK + 5, MSG_INFO = MSG_BACK + 6, MSG_START_COMMS = MSG_BACK + 7,
            MSG_FINISH_COMMS = MSG_BACK + 8, MSG_FINISH_ERROR_COMMS = MSG_BACK + 9, MSG_START_PRINT = MSG_BACK + 10,
            MSG_FINISH_PRINT = MSG_BACK + 11;
    /**
     * show logs
     */
    public static final int SHOWLOG = 1;
    public static final int ShowToastFlag = 106;//Pop up Toast message
    public static final int Hide_Progress = 10001;//New thread hidden progress bar
    public static final String SHA256 = "SHA-256";
    public static final int KEY_ID = 1;
    public static final String APP_NAME = "app1";
    public static final int AREA = 2;
    public static final int TMK_INDEX = 1;
    public static final int GROUP_ID = 1;
    public static final int TIK_ID = 0;

    public static final int PURCHASE = 1;
    public static final int BALANCE = 2;
    public static final int REFUND = 3;
    public static final int BANK_CARD = 1;

    public static final String TRANS_INFO = "TRANS_INFO";
    public static final String TRAN_TYPE = "tranType";
    public static final String TRAN_AMT = "orderAmount";
    public static final String PAYMENT_INSTRUMENT = "paymentInstrument";
    public static final String ACCT_TYPE = "ACCT_TYPE";
    public static final String DEFAULT_ACCT_TYPE = "00";
    public static final String SAVINGS_ACCT_TYPE = "10";
    public static final String CURRENT_ACCT_TYPE = "20";
    public static final String CREDIT_ACCT_TYPE = "30";
    public static final int DOWNLOAD_KEYS_JOB_ID = 1;
    public static final int CALL_HOME_JOB_ID = 2;
    public static final int REVERSAL_JOB_ID =  3;
    public static final String NEXT_ACTIVITY = "NEXT_ACTIVITY";
    public static final int REFUND_ACTIVITY = 1;
    public static final int REPRINT_ACTIVITY = 2;
    public static final int EOD_ACTIVITY = 3;
    public static final int ADMIN_ACTIVITY = 4;


    public static final String RSA_KEY_ALIAS = "httpsCert";
    public static final String LOGIN = "LOGIN"; /*login*/
    public static final String SETPINKEY = "SETPINKEY";
    public static final String SETMACKEY = "SETMACKEY";
    public static final String SETTMKKEY = "SETTMKKEY";
    public static final String SETKEYDOWNLOADTODAY = "SETKEYDOWNLOADTODAY"; /*set key download today*/
    public static final String KEYDOWNLOADDATE = "KEYDOWNLOADDATE"; /*tmk index for pinpad version 2.0 and 3.0*/
    public static final long DEFAULTKEYDOWNLOADDATE = 1517417789;
    public static final String KEYSLOADED = "KEYSLOADED";
    public static final String LOCALMASTERKEYLOADED = "LOCALMASTERKEYLOADED";
    public static final String EMVPARAMSLOADED = "EMVPARAMSLOADED";
    public static final String TERMPARAMSLOADED = "TERMPARAMSLOADED";
    public static final String AID_LOADED = "AID_LOADED";
    public static final String CAPK_LOADED = "CAPK_LOADED";
    public static final String MERCHANT_LOC = "MERCHANT_LOC";
    public static final String TERMINAL_ID = "TERMINAL_ID";
    public static final String MERCHANT_ID = "MERCHANT_ID";
    public static final String MERCHANT_NAME = "MERCHANT_NAME";
    public static final String MERCHANT_CATEGORY_CODE = "MERCHANT_CATEGORY_CODE";
    public static final String PURCHASE_SURCHARGE = "PURCHASE_SURCHARGE";
    public static final String MASK_CARD_NO = "MASK_CARD_NO";
    public static final String EXP_DATE = "EXP_DATE";
    public static final String CUST_NAME = "CUST_NAME";
    public static final String AMT = "AMT";
    public static final String STAN = "STAN";
    public static final String AUTH_NUM = "AUTH_NUM";
    public static final String REF_NO = "REF_NO";
    public static final String ACQUIRER_ID = "ACQUIRER_ID";
    public static final String PTSP = "PTSP";
    public static final String RETRIEVAL_REF = "RETRIEVAL_REF";
    public static final String CURRENCY_CODE = "CURRENCY_CODE";
    public static final String COUNTRY_CODE = "COUNTRY_CODE";
    public static final String POS_DATA_CODE = "POS_DATA_CODE";
    public static final String CTMS_HOST = "CTMS_HOST";
    public static final String CTMS_IP = "CTMS_IP";
    public static final String CTMS_PORT = "CTMS_PORT";
    public static final String CTMS_TIMEOUT = "CTMS_TIMEOUT";
    public static final String CTMS_SSL = "CTMS_SSL";
    public static final String TMS_HOST = "TMS_HOST";
    public static final String TMS_PORT = "TMS_PORT";
    public static final String TMS_TIMEOUT = "TMS_TIMEOUT";
    public static final String TMS_SSL = "TMS_SSL";
    public static final String ICC_DATA = "ICC_DATA";
    public static final String KEY_DOWNLOAD_TIME_IN_MINS = "KEY_DOWNLOAD_TIME_IN_MINS";
    public static final String CHECK_KEY_DOWNLOAD_INTERVAL_IN_MINS = "CHECK_KEY_DOWNLOAD_INTERVAL_IN_MINS";
    public static final String CALL_HOME_TIME_IN_MINS = "CALL_HOME_TIME_IN_MINS";
    public static final String RESEND_REVERSAL_TIME_IN_MINS =  "RESEND_REVERSAL_TIME_IN_MINS";
    public static final String PAGE_TIMER_IN_SEC = "PAGE_TIMER_IN_SEC";
    public static final String CTMK = "CTMK";
    public static final String TMK = "TMK";
    public static final String TSK = "TSK";
    public static final String FIRST_LAUNCH = "FIRST_LAUNCH";
    public static final String STATUS = "STATUS";
    public static final String APP_LABEL = "APP_LABEL";
    public static final String NETWORK_RESP_DATA = "NETWORK_RESP_DATA";
    public static final String CARD_TYPE = "CARD_TYPE";
    public static final String DEVICE_SERIAL_NO = "deviceSerialNo";
    public static final String TERMINAL_PUBLIC_KEY = "devicePublicKey";
    public static final String SUPERVISOR_PIN = "SUPERVISOR_PIN";
    public static final String ADMIN_PWD = "ADMIN_PWD";
    public static final String USE_REMOTE_NETWORK_CONFIG = "USE_REMOTE_NETWORK_CONFIG";

    public static final String TERMINAL_DOWNLOAD_URI = "/api/v1/terminals/parameters";

    public static final String NETWORK_REQ_TYPE = "NETWORK_REQ_TYPE";
    public static final int NETWORK_TMS_TERM_PARAM_DOWNLOAD_REQ_TYPE = 1;
    public static final int NETWORK_TMK_DOWNLOAD_REQ_TYPE = 2;
    public static final int NETWORK_TPK_DOWNLOAD_REQ_TYPE = 3;
    public static final int NETWORK_TSK_DOWNLOAD_REQ_TYPE = 4;
    public static final int NETWORK_IPEK_TRACK2_DOWNLOAD_REQ_TYPE = 5;
    public static final int NETWORK_IPEK_EMV_DOWNLOAD_REQ_TYPE = 6;
    public static final int NETWORK_CAPK_DOWNLOAD_REQ_TYPE = 7;
    public static final int NETWORK_AID_DOWNLOAD_REQ_TYPE = 8;
    public static final int NETWORK_NIBSS_TERM_PARAM_DOWNLOAD_REQ_TYPE = 9;
    public static final int NETWORK_EOD_DOWNLOAD_REQ_TYPE = 10;
    public static final int NETWORK_PURCHASE_REQ_TYPE = 11;
    public static final int NETWORK_PURCHASE_REQ_REVERSAL_TYPE = 12;
    public static final int NETWORK_BAL_REQ_TYPE = 13;
    public static final int NETWORK_REFUND_REQ_TYPE = 14;
    public static final int NETWORK_REFUND_REQ_REVERSAL_TYPE = 15;

    public static final int INIT_COMMU = 0;
    public static final int CONNECTING = 1;
    public static final int SENDING = 2;
    public static final int RECVING = 3;
    public static final int FINISH = 4;


    public static final String _0800 = "0800";
    public static final String _0100 = "0100";
    public static final String _0200 = "0200";
    public static final String _0420 = "0420";
    public static final String _0421 = "0421";
    public static final String TMK_DOWNLOAD_PROC_CODE = "9A0000";
    public static final String TSK_DOWNLOAD_PROC_CODE = "9B0000";
    public static final String TERM_PARAM_DOWNLOAD_PROC_CODE = "9C0000";
    public static final String CALL_HOME_PROC_CODE = "9D0000";
    public static final String CAPK_DOWNLOAD_PROC_CODE = "9E0000";
    public static final String AID_DOWNLOAD_PROC_CODE = "9F0000";
    public static final String TPK_DOWNLOAD_PROC_CODE = "9G0000";
    public static final String DAILY_REPORT_DOWNLOAD_PROC_CODE = "9H0000";
    public static final String IPEK_TRACK2_DOWNLOAD_PROC_CODE = "9I0000";
    public static final String IPEK_EMV_DOWNLOAD_PROC_CODE = "9J0000";
    public static final String PURCHASE_PROC_CODE = "00";
    public static final String BALANCE_PROC_CODE = "31";
    public static final String REFUND_PROC_CODE = "20";
    public static final String ACCEPT_PIN_MODE_CAPABILITY = "1";
    public static final String NORMAL_PRESENTMENT_POS_CONDITION_CODE = "00";

    public static final String ISO8583_APPROVED = "00";
    public static final String MSG_REASON_CODE_TIMEOUT_WAITING_FOR_RESPONSE = "4021";

    // Displayed message
    public static final String APPROVED = "APPROVED";
    public static final String DECLINED = "DECLINED";
    public static final String TRANSACTION_APPROVED = "Transaction Approved";
    public static final String TRANSACTION_DECLINED = "Transaction Declined";
    public static final String CARD_REMOVED = "Card has been removed";
    public static final String INVALID_CARD = "Invalid Card";
    public static final String REMOVE_CARD = "Please Remove Card";
    public static final String INSERT_CARD = "Insert or Swipe Card";
    public static final String WRONG_PIN = "Wrong PIN, Card Restricted/Blocked (3/3)";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds";
    public static final String DO_NOT_REMOVE_CARD = "Please do not remove card";
    public static final String WAITING_MSG = "EMV-Process waiting…";
    public static final String PRESS_PRINT_MERCHANT_COPY = "Press any where to print Merchant's copy";


    public static final int PIN_TIMEOUT = 10;
    public static final int MIN_PIN_LENGTH = 4;
    public static final int MAX_PIN_LENGTH = 4;
    public static final int PIN_BLOCK_FORMAT = 0x01;
    public static final int SUPPORT_PIN_BYPASS = 1;

    //EMV Things
    public static final int GOODS_TRAN_TYPE = 0x02;
    public static final int GET_PIN_DATA = 0x01;
    public static final String TERM_TRANSACTION_QUALITY = "26800080";
    public static final int ECTSI_ZERO = 0x00;
    public static final int ECTSI_ONE = 0x01;
    public static final int FORCE_ONLINE_TRUE = 0x01;
    public static final String F55 = "9F26,9F27,9F10,9F37,9F36,95,9A,9C,9F02,5F2A,82,9F1A,9F03,9F33";
    public static final String ex_title = "9F34,9F35,9F1E,91,9F63";
    public static final String onLineTag = ",74,8A";
    public static final String F55_EX = "5F24,57,5A,5F34,50,9F12,4F,9F06,84,9F09,8C,8D,9F4E,9F21,9B,9F41,EFA0,EFA1,8F,5F20";

    public static final int EMV_OPERATION_SUCCESS = 0x00;// 成功结果
    public static final int EMV_OPERATION_CANCEL = 0x01;// 成功结果
    public static final int EMV_OPERATION_ERR_END = 0x02;// 错误

    public static final int EMV_SALE = 0x02;// 成功结果
    public static final int EMV_CARD_TYPE_ICC = 0x01;
    public static final int EMV_CARD_TYPE_OTHER = 0x02;
    public static final int EMV_TAG_AID = 0x4F;

    public static final int OFFLINEAPPROVED       = 0x01    ;    //脱机成功
    public static final int OFFLINEDECLINED     = 0x02       ;    //脱机拒绝
    public static final int ONLINEAPPROVED      = 0x03       ;    //联机成功
    public static final int ONLINEDECLINED     = 0x04        ;    //联机拒绝
    public static final int UNABLEONLINE_OFFLINEAPPROVED= 0x05;    //联机失败 脱机成功
    public static final int UNABLEONINE_OFFLINEDECLINED= 0x06 ;    //联机失败 脱机拒绝

    /**
     * ic 卡 处理结果
     */
    //联机返回码处理
    public static final int ONLINE_APPROVE    = 0x00,      //联机批准(发卡行批准交易)
            ONLINE_FAILED     = 0x01,      //联机失败
            ONLINE_REFER      = 0x02,      //联机参考(发卡行参考)
            ONLINE_DENIAL     = 0x03,      //联机拒绝(发卡行拒绝交易)
            ONLINE_ABORT      = 0x04,      //终止交易

    //处理发卡行发起的参考
    REFER_APPROVE     = 0x01,      //参考返回码(选择批准) 接受交易
            REFER_DENIAL      = 0x02;     //参考返回码(选择拒绝) 拒绝交易


    /**
     * emvCore 交易结果
     */
    public static final String ARC_ONLINEAPPROVED        =    "00";
    public static final String ARC_OFFLINEAPPROVED       =    "Y1";   //脱机成功
    public static final String ARC_OFFLINEDECLINED       =    "Z1";   //脱机拒绝
    public static final String ARC_REFERRALAPPROVED      =    "Y2";   //
    public static final String ARC_REFERRALDECLINED      =    "Z2";   //
    public static final String ARC_ONLINEFAILOFFLINEAPPROVED ="Y3";   //联机失败 脱机成功
    public static final String ARC_ONLINEFAILOFFLINEDECLINED ="Z3";   //联机失败 脱机拒绝

    /**
     * app path
     */
    public static final int PATH_PBOC  = 0x00;    //Application Path: Standard PBOC
    public static final int PATH_QPBOC = 0x01;     //Application path: qPBOC
    public static final int PATH_MSD   = 0x02;     //Application Path: MSD
    public static final int PATH_ECash = 0x03;     //Application Path: Electronic Cash


    public static final int Type_OffLine_Sale=99;//Offline
    private static final int Base = 100;//Online
    /** Sale */
    public static final int Type_Sale = Base + 1;
    /** Consumer Reversal */
    public static final int Type_Void = Base + 2;
    /** Refunds */
    public static final int Type_Refund = Base + 3;
    /** Pre-authorization completed */
    public static final int Type_AuthComplete = Base + 4;
    /** Pre-authorization to complete the reversal */
    public static final int Type_CompleteVoid = Base + 5;
    public static final int Type_CoilingSale = Base + 10;//圈存
    public static final int Type_QueryBalance = Base +11;
    /** Pre-authorization */
    public static final int Type_Auth = Base + 21;
    /** Pre-authorized reversal */
    public static final int Type_Cancel = Base + 22;


    public static final String MAG_CARD_TYPE = "02";
    public static final String ICC_CARD_TYPE = "05";
    public static final String PICC_CARD_TYPE = "07";



    public static String[] CAPK_DATA = {
            "9F0605A0000000049F220100DF05083230303931323331DF060101DF070101DF02609E15214212F6308ACA78B80BD986AC287516846C8D548A9ED0A42E7D997C902C3E122D1B9DC30995F4E25C75DD7EE0A0CE293B8CC02B977278EF256D761194924764942FE714FA02E4D57F282BA3B2B62C9E38EF6517823F2CA831BDDF6D363DDF040103DF03148BB99ADDF7B560110955014505FB6B5F8308CE27",
            "9F0605A0000000049F220101DF05083230303931323331DF060101DF070101DF0260D2010716C9FB5264D8C91A14F4F32F8981EE954F20087ED77CDC5868431728D3637C632CCF2718A4F5D92EA8AB166AB992D2DE24E9FBDC7CAB9729401E91C502D72B39F6866F5C098B1243B132AFEE65F5036E168323116338F8040834B98725DF040103DF0314EA950DD4234FEB7C900C0BE817F64DE66EEEF7C4",
            "9F0605A0000000049F220102DF05083230303931323331DF060101DF070101DF0270CF4264E1702D34CA897D1F9B66C5D63691EACC612C8F147116BB22D0C463495BD5BA70FB153848895220B8ADEEC3E7BAB31EA22C1DC9972FA027D54265BEBF0AE3A23A8A09187F21C856607B98BDA6FC908116816C502B3E58A145254EEFEE2A3335110224028B67809DCB8058E24895DF040103DF0314AF1CC1FD1C1BC9BCA07E78DA6CBA2163F169CBB7",
            "9F0605A0000000049F220104DF05083230313731323331DF060101DF070101DF028190A6DA428387A502D7DDFB7A74D3F412BE762627197B25435B7A81716A700157DDD06F7CC99D6CA28C2470527E2C03616B9C59217357C2674F583B3BA5C7DCF2838692D023E3562420B4615C439CA97C44DC9A249CFCE7B3BFB22F68228C3AF13329AA4A613CF8DD853502373D62E49AB256D2BC17120E54AEDCED6D96A4287ACC5C04677D4A5A320DB8BEE2F775E5FEC5DF040103DF0314381A035DA58B482EE2AF75F4C3F2CA469BA4AA6C",
            "9F0605A0000000049F220105DF05083230323131323331DF060101DF070101DF0281B0B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597DF040103DF0314EBFA0D5D06D8CE702DA3EAE890701D45E274C845",
            "9F0605A0000000049F220106DF05083230323131323331DF060101DF070101DF0281F8CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747FDF040103DF0314F910A1504D5FFB793D94F3B500765E1ABCAD72D9",
            "9F0605A0000000049F220109DF05083230323131323331DF060101DF070101DF0260967B6264436C96AA9305776A5919C70DA796340F9997A6C6EF7BEF1D4DBF9CB4289FB7990ABFF1F3AE692F12844B2452A50AE075FB327976A40E8028F279B1E3CCB623957D696FC1225CA2EC950E2D415E9AA931FF18B13168D661FBD06F0ABBDF040103DF03141D90595C2EF9FC6E71B0C721118333DF8A71FE21",
            "9F0605A0000000049F220122DF05083230323131323331DF060101DF070101DF0260BBE43877CC28C0CE1E14BC14E8477317E218364531D155BB8AC5B63C0D6E284DD24259193899F9C04C30BAF167D57929451F67AEBD3BBD0D41444501847D8F02F2C2A2D14817D97AE2625DC163BF8B484C40FFB51749CEDDE9434FB2A0A41099DF040103DF0314008C39B1D119498268B07843349427AC6E98F807",
            "9F0605A0000000049F220152DF05083230323131323331DF060101DF070101DF028180B831414E0B4613922BD35B4B36802BC1E1E81C95A27C958F5382003DF646154CA92FC1CE02C3BE047A45E9B02A9089B4B90278237C965192A0FCC86BB49BC82AE6FDC2DE709006B86C7676EFDF597626FAD633A4F7DC48C445D37EB55FCB3B1ABB95BAAA826D5390E15FD14ED403FA2D0CB841C650609524EC555E3BC56CA957DF040103DF0314DEB81EDB2626A4BB6AE23B77D19A77539D0E6716"
    };

    public static String[] AID_DATA = {
            // 9F08020002
            "9F0607A0000000041010DF0101009F0802008CDF1105FCF8FCF870DF1205FCF8FCF8F0DF1305FCF8F800709F1B0430303030DF150430303030DF160100DF170100DF14029F37DF1801019F7B06000000000000DF1906000000000000DF2006000000000000DF2106000000000000",
            "9F0607A0000000041010DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000"
    };


    //
    //

    /*
    public static String[] CAPK_DATA = {
            "9F0605A0000000049F220100DF05083230303931323331DF060101DF070101DF02609E15214212F6308ACA78B80BD986AC287516846C8D548A9ED0A42E7D997C902C3E122D1B9DC30995F4E25C75DD7EE0A0CE293B8CC02B977278EF256D761194924764942FE714FA02E4D57F282BA3B2B62C9E38EF6517823F2CA831BDDF6D363DDF040103DF03148BB99ADDF7B560110955014505FB6B5F8308CE27",
            "9F0605A0000000049F220101DF05083230303931323331DF060101DF070101DF0260D2010716C9FB5264D8C91A14F4F32F8981EE954F20087ED77CDC5868431728D3637C632CCF2718A4F5D92EA8AB166AB992D2DE24E9FBDC7CAB9729401E91C502D72B39F6866F5C098B1243B132AFEE65F5036E168323116338F8040834B98725DF040103DF0314EA950DD4234FEB7C900C0BE817F64DE66EEEF7C4",
            "9F0605A0000000049F220102DF05083230303931323331DF060101DF070101DF0270CF4264E1702D34CA897D1F9B66C5D63691EACC612C8F147116BB22D0C463495BD5BA70FB153848895220B8ADEEC3E7BAB31EA22C1DC9972FA027D54265BEBF0AE3A23A8A09187F21C856607B98BDA6FC908116816C502B3E58A145254EEFEE2A3335110224028B67809DCB8058E24895DF040103DF0314AF1CC1FD1C1BC9BCA07E78DA6CBA2163F169CBB7",
            "9F0605A0000000049F220103DF05083230303931323331DF060101DF070101DF028180C2490747FE17EB0584C88D47B1602704150ADC88C5B998BD59CE043EDEBF0FFEE3093AC7956AD3B6AD4554C6DE19A178D6DA295BE15D5220645E3C8131666FA4BE5B84FE131EA44B039307638B9E74A8C42564F892A64DF1CB15712B736E3374F1BBB6819371602D8970E97B900793C7C2A89A4A1649A59BE680574DD0B60145DF040103DF03145ADDF21D09278661141179CBEFF272EA384B13BB",
            "9F0605A0000000049F220104DF05083230313731323331DF060101DF070101DF028190A6DA428387A502D7DDFB7A74D3F412BE762627197B25435B7A81716A700157DDD06F7CC99D6CA28C2470527E2C03616B9C59217357C2674F583B3BA5C7DCF2838692D023E3562420B4615C439CA97C44DC9A249CFCE7B3BFB22F68228C3AF13329AA4A613CF8DD853502373D62E49AB256D2BC17120E54AEDCED6D96A4287ACC5C04677D4A5A320DB8BEE2F775E5FEC5DF040103DF0314381A035DA58B482EE2AF75F4C3F2CA469BA4AA6C",
            "9F0605A0000000049F220105DF05083230323131323331DF060101DF070101DF0281B0B8048ABC30C90D976336543E3FD7091C8FE4800DF820ED55E7E94813ED00555B573FECA3D84AF6131A651D66CFF4284FB13B635EDD0EE40176D8BF04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B8E70B2DDD811CB4196525EA386ACC33C0D9D4575916469C4E4F53E8E1C912CC618CB22DDE7C3568E90022E6BBA770202E4522A2DD623D180E215BD1D1507FE3DC90CA310D27B3EFCCD8F83DE3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597DF040103DF0314EBFA0D5D06D8CE702DA3EAE890701D45E274C845",
            "9F0605A0000000049F220106DF05083230323131323331DF060101DF070101DF0281F8CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747FDF040103DF0314F910A1504D5FFB793D94F3B500765E1ABCAD72D9",
            "9F0605A0000000049F220109DF05083230323131323331DF060101DF070101DF0260967B6264436C96AA9305776A5919C70DA796340F9997A6C6EF7BEF1D4DBF9CB4289FB7990ABFF1F3AE692F12844B2452A50AE075FB327976A40E8028F279B1E3CCB623957D696FC1225CA2EC950E2D415E9AA931FF18B13168D661FBD06F0ABBDF040103DF03141D90595C2EF9FC6E71B0C721118333DF8A71FE21",
            "9F0605A0000000049F220122DF05083230323131323331DF060101DF070101DF0260BBE43877CC28C0CE1E14BC14E8477317E218364531D155BB8AC5B63C0D6E284DD24259193899F9C04C30BAF167D57929451F67AEBD3BBD0D41444501847D8F02F2C2A2D14817D97AE2625DC163BF8B484C40FFB51749CEDDE9434FB2A0A41099DF040103DF0314008C39B1D119498268B07843349427AC6E98F807",
            "9F0605A0000000049F220152DF05083230323131323331DF060101DF070101DF028180B831414E0B4613922BD35B4B36802BC1E1E81C95A27C958F5382003DF646154CA92FC1CE02C3BE047A45E9B02A9089B4B90278237C965192A0FCC86BB49BC82AE6FDC2DE709006B86C7676EFDF597626FAD633A4F7DC48C445D37EB55FCB3B1ABB95BAAA826D5390E15FD14ED403FA2D0CB841C650609524EC555E3BC56CA957DF040103DF0314DEB81EDB2626A4BB6AE23B77D19A77539D0E6716",
            "9F0605A0000000039F220103DF05083230353031323331DF060101DF070101DF0270B3E5E667506C47CAAFB12A2633819350846697DD65A796E5CE77C57C626A66F70BB630911612AD2832909B8062291BECA46CD33B66A6F9C9D48CED8B4FC8561C8A1D8FB15862C9EB60178DEA2BE1F82236FFCFF4F3843C272179DCDD384D541053DA6A6A0D3CE48FDC2DC4E3E0EEE15FDF040103DF0314FE70AB3B4D5A1B9924228ADF8027C758483A8B7E",
            "9F0605A0000000039F220105DF05083230353031323331DF060101DF070101DF0260D0135CE8A4436C7F9D5CC66547E30EA402F98105B71722E24BC08DCC80AB7E71EC23B8CE6A1DC6AC2A8CF55543D74A8AE7B388F9B174B7F0D756C22CBB5974F9016A56B601CCA64C71F04B78E86C501B193A5556D5389ECE4DEA258AB97F52A3DF040103DF031486DF041E7995023552A79E2623E49180C0CD957A",
            "9F0605A0000000039F220108DF05083230323431323331DF060101DF070101DF0281B0D9FD6ED75D51D0E30664BD157023EAA1FFA871E4DA65672B863D255E81E137A51DE4F72BCC9E44ACE12127F87E263D3AF9DD9CF35CA4A7B01E907000BA85D24954C2FCA3074825DDD4C0C8F186CB020F683E02F2DEAD3969133F06F7845166ACEB57CA0FC2603445469811D293BFEFBAFAB57631B3DD91E796BF850A25012F1AE38F05AA5C4D6D03B1DC2E568612785938BBC9B3CD3A910C1DA55A5A9218ACE0F7A21287752682F15832A678D6E1ED0BDF040103DF031420D213126955DE205ADC2FD2822BD22DE21CF9A8"
    };

    public static String[] AID_DATA = {
            // 9F08020002
            "9F0607A0000000041010DF0101009F0802008CDF1105FCF8FCF870DF1205FCF8FCF8F0DF1305FCF8F800709F1B0430303030DF150430303030DF160100DF170100DF14029F37DF1801019F7B06000000000000DF1906000000000000DF2006000000000000DF2106000000000000"
            //"9F0607A0000000041010DF0101019F08020002DF1105FC50ACA000DF1205F850ACF800DF130504000000009F1B0431303030DF150435303030DF160100DF170100DF140B9F37049F47018F019F3201DF1801019F7B06000000000100DF1906000000100000DF2006000000100000"
    };
    */


    /*
    Message reason codes
    1003 Card issuer unavailable
1006 Under floor limit
1376 PIN verification failure
1377 Change dispensed
1378 IOU receipt printed
1510 Over floor limit
1800 Negative card
4000 Customer cancellation
4001 Unspecified, no action taken
4004 Completed partially
4021 Timeout waiting for response
     */
}
