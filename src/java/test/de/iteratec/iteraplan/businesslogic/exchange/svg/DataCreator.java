/*
 * iteraplan is an IT Governance web application developed by iteratec, GmbH
 * Copyright (C) 2004 - 2014 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact iteratec GmbH headquarters at Inselkammerstr. 4
 * 82008 Munich - Unterhaching, Germany, or at email address info@iteratec.de.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "iteraplan" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by iteraplan".
 */
package de.iteratec.iteraplan.businesslogic.exchange.svg;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;


public final class DataCreator {

  public static final String        ATG_TEST_ATS            = "TestAttributesGroup";

  public static final String        AT_HEALTH               = "Health";
  public static final String        AT_HEALTH_LITERAL_GOOD  = "Good";
  public static final String        AT_HEALTH_LITERAL_AVG   = "Average";
  public static final String        AT_HEALTH_LITERAL_BAD   = "Bad";

  public static final String        AT_COSTS                = "Costs";
  public static final String        AT_INTRODUCTION_DATE    = "IntroductionDate";
  public static final String        AT_EVALUATION_DATE      = "EvaluationDate";
  public static final String        AT_DECISION_DATE        = "DecisionDate";

  public static final String        DI_INTRO_TO_EVAL        = "Intro2Eval";
  public static final String        DI_EVAL_TO_DECISION     = "Eval2Decision";
  public static final String        DI_INTRO_TO_DECISION    = "Intro2Decision";

  public static final String        IS_CRM                  = "CRM";
  public static final String        IS_SAP                  = "SAP";
  public static final String        IS_SAP_PI               = "SAP PI";
  public static final String        IS_SAP_FI               = "SAP FI";
  public static final String        IS_FUNDS                = "Funds";
  public static final String        IS_DMS                  = "DMS";
  public static final String        IS_DWH                  = "DWH";
  public static final String        IS_ELECTRONIC_BANKING   = "Electronic Banking";
  public static final String        IS_SCM                  = "SCM";

  public static final String        TC_WINDOWS              = "Windows";
  public static final String        TC_WIN2K                = "Windows 2000";
  public static final String        TC_WIN7                 = "Windows 7";
  public static final String        TC_JAVA                 = "Java";
  public static final String        TC_JAVA1_4              = "Java 1.4";
  public static final String        TC_JAVA5                = "Java 5";
  public static final String        TC_JAVA6                = "Java 6";
  public static final String        TC_ORACLE               = "Oracle";

  public static final String        AD_APPLICATION_SERVER   = "Application Server";
  public static final String        AD_DATABASE             = "Database";
  public static final String        AD_PROGRAMMING_LANGUAGE = "Programming Language";
  public static final String        AD_OPERATING_SYSTEM     = "Operating System";

  private static final String       PROJ_CRM                = "CRM in Cloud";
  private static final String       PROJ_DB_BENCHMARK       = "DB Benchmarking";
  private static final String       PROJ_MIGRATION_DB       = "Migration DB";
  private static final String       PROJ_CONSOLIDATION      = "Consolidation of Banking Core";
  private static final String       PROJ_CENTRALIZATION     = "Centralization of IT";

  private static final String       PROD_CREDIT_CARD        = "Credit Card";
  private static final String       PROD_LOAN               = "Loan";
  private static final String       PROD_SAVINGS_BOOK       = "Savings Book";
  private static final String       PROD_SPECIAL_AGREEMENT  = "Special Agreement";

  private static final String       IE_CLUSTER_1            = "Cluster 1";
  private static final String       IE_CLUSTER_2            = "Cluster 2";
  private static final String       IE_IBM_HOST             = "IBM Host";
  private static final String       IE_SERVER_760           = "Server 760";
  private static final String       IE_SERVER_37S           = "Server 37s";

  private static final String       BU_CONTROLLING          = "Controlling";
  private static final String       BU_COMPLIANCE           = "Compliance";
  private static final String       BU_FINANCE              = "Finance";
  private static final String       BU_IT_OPERATIONS        = "IT & Operations";

  private static final String       BF_BALANCE_ACCOUNT      = "Balance Account";
  private static final String       BF_CLEARING             = "Clearing";
  private static final String       BF_DECISION_SUPPORT     = "Decision Support";
  private static final String       BF_MARKETING            = "Marketing";
  private static final String       BF_STRATEGIC_DEV        = "Strategic Development";
  private static final String       BF_TX_HANDLING          = "TX Handling";

  private static final String       BP_MARKETING            = "Marketing";

  private static final Date         DATE_1                  = parseDate("01.01.2011");
  private static final Date         DATE_2                  = parseDate("01.01.2012");
  private static final Date         DATE_3                  = parseDate("01.01.2013");
  private static final Date         DATE_4                  = parseDate("01.01.2014");
  private static final Date         DATE_5                  = parseDate("01.06.2013");
  private static final Date         DATE_6                  = parseDate("15.03.2014");
  private static final Date         DATE_7                  = parseDate("01.07.2012");
  private static final Date         DATE_8                  = parseDate("01.10.2012");
  private static final Date         DATE_9                  = parseDate("01.03.2023");

  private final TestDataHelper2     dataHelper;

  private DateAT                    introDate;
  private DateAT                    evalDate;
  private DateAT                    decisionDate;
  private NumberAT                  costs;

  private EnumAV                    healthGood;
  private EnumAV                    healthAvg;
  private EnumAV                    healthBad;

  private InformationSystemRelease  isCrm31;
  private InformationSystemRelease  isCrm32;
  private InformationSystemRelease  isSap;
  private InformationSystemRelease  isSapPi;
  private InformationSystemRelease  isSapFi;
  private InformationSystemRelease  isFunds;
  private InformationSystemRelease  isDms;
  private InformationSystemRelease  isDwh;
  private InformationSystemRelease  isElBanking;
  private InformationSystemRelease  isScm;

  private TechnicalComponentRelease tcrWin;
  private TechnicalComponentRelease tcrWin2k;
  private TechnicalComponentRelease tcrWin7;
  private TechnicalComponentRelease tcrJava;
  private TechnicalComponentRelease tcrJava14;
  private TechnicalComponentRelease tcrJava5;
  private TechnicalComponentRelease tcrJava6;
  private TechnicalComponentRelease tcrOracle;

  private ArchitecturalDomain       adDatabase;
  private ArchitecturalDomain       adOperatingSystem;
  private ArchitecturalDomain       adProgrammingLanguage;

  private Project                   projCrm;
  private Project                   projDbBenchmark;
  private Project                   projMigrationDb;
  private Project                   projConsolidation;
  private Project                   projCentralization;

  private Product                   prodCreditCard;
  private Product                   prodLoan;
  private Product                   prodSavingsBook;
  private Product                   prodSpecialAgreement;

  private InfrastructureElement     ieCluster1;
  private InfrastructureElement     ieCluster2;
  private InfrastructureElement     ieIbmHost;
  private InfrastructureElement     ieServer760;
  private InfrastructureElement     ieServer37s;

  private BusinessUnit              buControlling;
  private BusinessUnit              buCompliance;
  private BusinessUnit              buFinance;
  private BusinessUnit              buItOperations;

  private BusinessFunction          bfBalanceAccount;
  private BusinessFunction          bfClearing;
  private BusinessFunction          bfDecisionSupport;
  private BusinessFunction          bfMarketing;
  private BusinessFunction          bfStrategicDev;
  private BusinessFunction          bfTxHandling;

  private BusinessProcess           bpMarketing;

  public DataCreator(TestDataHelper2 dataHelper) {
    this.dataHelper = dataHelper;
  }

  public void createData() {
    createAttributeTypes();
    createDateIntervals();

    createInformationSystemReleases();
    createTechnicalComponents();
    createArchitecturalDomains();
    createProjects();
    createProducts();
    createInfrastructureElements();
    createBusinessUnits();
    createBusinessFunctions();
    createBusinessProcesses();

    createIsrSelfRelationships();
    createTcrSelfRelationships();

    createBusinessMappings();
    createTcr2IeAssociations();

    connectBf2IIsr();
    connectAd2Tc();
    connectTc2Isr();
    connectProj2Isr();
    connectIsr2Ie();
  }

  private void createAttributeTypes() {
    AttributeTypeGroup testATG = dataHelper.createAttributeTypeGroup(ATG_TEST_ATS, description(ATG_TEST_ATS));

    costs = dataHelper.createNumberAttributeType(AT_COSTS, description(AT_COSTS), testATG);
    dataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(costs);

    introDate = dataHelper.createDateAttributeType(AT_INTRODUCTION_DATE, description(AT_INTRODUCTION_DATE), testATG);
    dataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(introDate);

    evalDate = dataHelper.createDateAttributeType(AT_EVALUATION_DATE, description(AT_EVALUATION_DATE), testATG);
    dataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(evalDate);

    decisionDate = dataHelper.createDateAttributeType(AT_DECISION_DATE, description(AT_DECISION_DATE), testATG);
    dataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(decisionDate);

    EnumAT health = dataHelper.createEnumAttributeType(AT_HEALTH, description(AT_HEALTH), Boolean.FALSE, testATG);
    dataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(health);
    healthGood = dataHelper.createEnumAV(AT_HEALTH_LITERAL_GOOD, description(AT_HEALTH_LITERAL_GOOD), "AFCEA8", health);
    healthAvg = dataHelper.createEnumAV(AT_HEALTH_LITERAL_AVG, description(AT_HEALTH_LITERAL_AVG), "ACA1C8", health);
    healthBad = dataHelper.createEnumAV(AT_HEALTH_LITERAL_BAD, description(AT_HEALTH_LITERAL_BAD), "E8BB9E", health);
  }

  private void createDateIntervals() {
    dataHelper.createDateInterval(DI_INTRO_TO_EVAL, Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR, introDate, evalDate);
    dataHelper.createDateInterval(DI_EVAL_TO_DECISION, Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR, evalDate, decisionDate);
    dataHelper.createDateInterval(DI_INTRO_TO_DECISION, Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR, introDate, decisionDate);

  }

  private void createInformationSystemReleases() {
    InformationSystem crm = dataHelper.createInformationSystem(IS_CRM);
    isCrm31 = dataHelper.createInformationSystemRelease(crm, "3.1");
    isCrm31.setRuntimePeriod(new RuntimePeriod(null, DATE_4));
    isCrm31.setTypeOfStatus(TypeOfStatus.CURRENT);
    dataHelper.createAVA(isCrm31, healthBad);
    dataHelper.createAVA(isCrm31, numberAV(costs, 28));
    dataHelper.createAVA(isCrm31, dateAV(introDate, DATE_1));
    dataHelper.createAVA(isCrm31, dateAV(evalDate, DATE_2));
    dataHelper.createAVA(isCrm31, dateAV(decisionDate, DATE_5));

    isCrm32 = dataHelper.createInformationSystemRelease(crm, "3.2");
    isCrm32.setTypeOfStatus(TypeOfStatus.CURRENT);
    isCrm32.setRuntimePeriod(new RuntimePeriod(DATE_3, null));
    dataHelper.createAVA(isCrm32, healthGood);
    dataHelper.createAVA(isCrm32, numberAV(costs, 35));
    dataHelper.createAVA(isCrm32, dateAV(introDate, DATE_3));
    dataHelper.createAVA(isCrm32, dateAV(evalDate, DATE_5));
    dataHelper.createAVA(isCrm32, dateAV(decisionDate, DATE_4));

    isSap = dataHelper.createInformationSystemRelease(dataHelper.createInformationSystem(IS_SAP), "1.10");
    isSap.setTypeOfStatus(TypeOfStatus.CURRENT);
    isSap.setRuntimePeriod(new RuntimePeriod(null, DATE_9));
    dataHelper.createAVA(isSap, healthBad);
    dataHelper.createAVA(isSap, numberAV(costs, 115));
    dataHelper.createAVA(isSap, dateAV(introDate, DATE_2));
    dataHelper.createAVA(isSap, dateAV(evalDate, DATE_9));
    dataHelper.createAVA(isSap, dateAV(decisionDate, DATE_5));

    isSapPi = dataHelper.createInformationSystemRelease(dataHelper.createInformationSystem(IS_SAP_PI), "6.58b");
    isSapPi.setTypeOfStatus(TypeOfStatus.PLANNED);
    isSapPi.setRuntimePeriod(new RuntimePeriod(DATE_6, null));
    dataHelper.createAVA(isSapPi, numberAV(costs, 87));
    dataHelper.createAVA(isSapPi, dateAV(introDate, DATE_5));
    dataHelper.createAVA(isSapPi, dateAV(evalDate, DATE_6));

    isSapFi = dataHelper.createInformationSystemRelease(dataHelper.createInformationSystem(IS_SAP_FI), "3.22");
    isSapFi.setTypeOfStatus(TypeOfStatus.CURRENT);
    isSapFi.setRuntimePeriod(new RuntimePeriod(DATE_5, null));
    dataHelper.createAVA(isSapFi, healthGood);
    dataHelper.createAVA(isSapFi, numberAV(costs, 42));
    dataHelper.createAVA(isSapFi, dateAV(introDate, DATE_1));
    dataHelper.createAVA(isSapFi, dateAV(evalDate, DATE_5));
    dataHelper.createAVA(isSapFi, dateAV(decisionDate, DATE_6));

    isFunds = dataHelper.createInformationSystemRelease(dataHelper.createInformationSystem(IS_FUNDS), "1.0");
    isFunds.setTypeOfStatus(TypeOfStatus.CURRENT);
    isFunds.setRuntimePeriod(new RuntimePeriod(null, DATE_9));
    dataHelper.createAVA(isFunds, healthBad);
    dataHelper.createAVA(isFunds, numberAV(costs, 12));
    dataHelper.createAVA(isFunds, dateAV(evalDate, DATE_7));
    dataHelper.createAVA(isFunds, dateAV(decisionDate, DATE_5));

    isDms = dataHelper.createInformationSystemRelease(dataHelper.createInformationSystem(IS_DMS), "11.9");
    isDms.setTypeOfStatus(TypeOfStatus.PLANNED);
    isDms.setRuntimePeriod(new RuntimePeriod(DATE_5, DATE_9));
    dataHelper.createAVA(isDms, numberAV(costs, 6));
    dataHelper.createAVA(isDms, dateAV(introDate, DATE_5));
    dataHelper.createAVA(isDms, dateAV(evalDate, DATE_9));

    isDwh = dataHelper.createInformationSystemRelease(dataHelper.createInformationSystem(IS_DWH), "1.09");
    isDwh.setTypeOfStatus(TypeOfStatus.CURRENT);
    dataHelper.createAVA(isDwh, healthAvg);
    dataHelper.createAVA(isDwh, numberAV(costs, 14));

    isElBanking = dataHelper.createInformationSystemRelease(dataHelper.createInformationSystem(IS_ELECTRONIC_BANKING), "1.24a");
    isElBanking.setTypeOfStatus(TypeOfStatus.CURRENT);
    isElBanking.setRuntimePeriod(new RuntimePeriod(null, DATE_4));
    dataHelper.createAVA(isElBanking, healthAvg);
    dataHelper.createAVA(isElBanking, numberAV(costs, 75));
    dataHelper.createAVA(isElBanking, dateAV(introDate, DATE_8));
    dataHelper.createAVA(isElBanking, dateAV(evalDate, DATE_5));
    dataHelper.createAVA(isElBanking, dateAV(decisionDate, DATE_6));

    isScm = dataHelper.createInformationSystemRelease(dataHelper.createInformationSystem(IS_SCM), "3");
    isScm.setTypeOfStatus(TypeOfStatus.CURRENT);
    isSap.setRuntimePeriod(new RuntimePeriod(DATE_7, null));
    dataHelper.createAVA(isScm, healthGood);
    dataHelper.createAVA(isScm, numberAV(costs, 23));
    dataHelper.createAVA(isScm, dateAV(evalDate, DATE_3));
    dataHelper.createAVA(isScm, dateAV(decisionDate, DATE_6));
  }

  private void createTechnicalComponents() {
    TechnicalComponent tcWin = dataHelper.createTechnicalComponent(TC_WINDOWS, true, true);
    tcrWin = dataHelper.createTCRelease(tcWin, "all", true);
    tcrWin.setTypeOfStatus(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.CURRENT);
    tcrWin.setRuntimePeriod(new RuntimePeriod(null, null));
    dataHelper.createAVA(tcrWin, healthAvg);

    tcrWin2k = dataHelper.createTCRelease(tcWin, "2000", true);
    tcrWin2k.setTypeOfStatus(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.INACTIVE);
    tcrWin2k.setRuntimePeriod(new RuntimePeriod(null, DATE_4));
    dataHelper.createAVA(tcrWin2k, numberAV(costs, 36));
    dataHelper.createAVA(tcrWin2k, healthBad);
    dataHelper.createAVA(tcrWin2k, dateAV(evalDate, DATE_3));
    dataHelper.createAVA(tcrWin2k, dateAV(decisionDate, DATE_5));

    tcrWin7 = dataHelper.createTCRelease(tcWin, "7", true);
    tcrWin7.setTypeOfStatus(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.CURRENT);
    tcrWin7.setRuntimePeriod(new RuntimePeriod(null, DATE_9));
    dataHelper.createAVA(tcrWin7, numberAV(costs, 45));
    dataHelper.createAVA(tcrWin7, healthGood);
    dataHelper.createAVA(tcrWin7, dateAV(introDate, DATE_1));
    dataHelper.createAVA(tcrWin7, dateAV(evalDate, DATE_2));
    dataHelper.createAVA(tcrWin7, dateAV(decisionDate, DATE_6));

    TechnicalComponent java = dataHelper.createTechnicalComponent(TC_JAVA, true, true);
    tcrJava = dataHelper.createTCRelease(java, "all", true);
    tcrJava.setTypeOfStatus(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.CURRENT);
    dataHelper.createAVA(tcrJava, healthAvg);

    tcrJava14 = dataHelper.createTCRelease(java, "1.4", true);
    tcrJava14.setTypeOfStatus(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.INACTIVE);
    tcrJava14.setRuntimePeriod(new RuntimePeriod(null, DATE_3));
    dataHelper.createAVA(tcrJava14, numberAV(costs, 8));
    dataHelper.createAVA(tcrJava14, healthBad);
    dataHelper.createAVA(tcrJava14, dateAV(evalDate, DATE_2));
    dataHelper.createAVA(tcrJava14, dateAV(decisionDate, DATE_6));

    tcrJava5 = dataHelper.createTCRelease(java, "5", true);
    tcrJava5.setTypeOfStatus(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.CURRENT);
    tcrJava5.setRuntimePeriod(new RuntimePeriod(DATE_2, null));
    dataHelper.createAVA(tcrJava5, numberAV(costs, 12));
    dataHelper.createAVA(tcrJava5, healthGood);
    dataHelper.createAVA(tcrJava5, dateAV(introDate, DATE_2));
    dataHelper.createAVA(tcrJava5, dateAV(evalDate, DATE_7));
    dataHelper.createAVA(tcrJava5, dateAV(decisionDate, DATE_6));

    tcrJava6 = dataHelper.createTCRelease(java, "6", true);
    tcrJava6.setTypeOfStatus(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.PLANNED);
    tcrJava6.setRuntimePeriod(new RuntimePeriod(DATE_3, null));
    dataHelper.createAVA(tcrJava6, numberAV(costs, 4));
    dataHelper.createAVA(tcrJava6, healthGood);
    dataHelper.createAVA(tcrJava6, dateAV(introDate, DATE_3));
    dataHelper.createAVA(tcrJava6, dateAV(evalDate, DATE_5));
    dataHelper.createAVA(tcrJava6, dateAV(decisionDate, DATE_4));

    tcrOracle = dataHelper.createTCRelease(dataHelper.createTechnicalComponent(TC_ORACLE, true, true), "11i", true);
    tcrOracle.setTypeOfStatus(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.TARGET);
    tcrOracle.setRuntimePeriod(new RuntimePeriod(DATE_4, DATE_9));
    dataHelper.createAVA(tcrOracle, numberAV(costs, 200));
    dataHelper.createAVA(tcrOracle, dateAV(introDate, DATE_4));
    dataHelper.createAVA(tcrOracle, dateAV(evalDate, DATE_6));
    dataHelper.createAVA(tcrOracle, dateAV(decisionDate, DATE_9));
  }

  private void createArchitecturalDomains() {
    adDatabase = dataHelper.createArchitecturalDomain(AD_DATABASE, description(AD_DATABASE));
    dataHelper.createAVA(adDatabase, healthAvg);
    dataHelper.createAVA(adDatabase, numberAV(costs, 200));
    dataHelper.createAVA(adDatabase, dateAV(introDate, DATE_4));

    ArchitecturalDomain adAppServer = dataHelper.createArchitecturalDomain(AD_APPLICATION_SERVER, description(AD_APPLICATION_SERVER));
    dataHelper.createAVA(adAppServer, healthGood);
    dataHelper.createAVA(adAppServer, numberAV(costs, 8));

    adOperatingSystem = dataHelper.createArchitecturalDomain(AD_OPERATING_SYSTEM, description(AD_OPERATING_SYSTEM));
    dataHelper.createAVA(adOperatingSystem, healthAvg);
    dataHelper.createAVA(adOperatingSystem, numberAV(costs, 80));
    dataHelper.createAVA(adOperatingSystem, dateAV(evalDate, DATE_4));
    dataHelper.createAVA(adOperatingSystem, dateAV(decisionDate, DATE_6));

    adProgrammingLanguage = dataHelper.createArchitecturalDomain(AD_PROGRAMMING_LANGUAGE, description(AD_PROGRAMMING_LANGUAGE));
    dataHelper.createAVA(adProgrammingLanguage, healthGood);
    dataHelper.createAVA(adProgrammingLanguage, numberAV(costs, 3));
  }

  private void createProjects() {
    projCrm = dataHelper.createProject(PROJ_CRM, description(PROJ_CRM));
    projCrm.setRuntimePeriod(new RuntimePeriod(null, null));
    dataHelper.createAVA(projCrm, healthGood);
    dataHelper.createAVA(projCrm, numberAV(costs, 20));
    dataHelper.createAVA(projCrm, dateAV(decisionDate, DATE_1));
    dataHelper.createAVA(projCrm, dateAV(evalDate, DATE_4));

    projDbBenchmark = dataHelper.createProject(PROJ_DB_BENCHMARK, description(PROJ_DB_BENCHMARK));
    projDbBenchmark.setRuntimePeriod(new RuntimePeriod(DATE_1, DATE_5));
    dataHelper.createAVA(projDbBenchmark, healthAvg);
    dataHelper.createAVA(projDbBenchmark, dateAV(introDate, DATE_2));
    dataHelper.createAVA(projDbBenchmark, dateAV(decisionDate, DATE_3));
    dataHelper.createAVA(projDbBenchmark, dateAV(evalDate, DATE_7));

    projCentralization = dataHelper.createProject(PROJ_CENTRALIZATION, description(PROJ_CENTRALIZATION));
    projCentralization.setRuntimePeriod(new RuntimePeriod(null, DATE_6));
    dataHelper.createAVA(projCentralization, healthGood);
    dataHelper.createAVA(projCentralization, numberAV(costs, 35));
    dataHelper.createAVA(projCentralization, dateAV(introDate, DATE_5));
    dataHelper.createAVA(projCentralization, dateAV(evalDate, DATE_6));

    projConsolidation = dataHelper.createProject(PROJ_CONSOLIDATION, description(PROJ_CONSOLIDATION));
    projConsolidation.setRuntimePeriod(new RuntimePeriod(DATE_4, DATE_9));
    dataHelper.createAVA(projConsolidation, healthBad);
    dataHelper.createAVA(projConsolidation, numberAV(costs, 16));

    projMigrationDb = dataHelper.createProject(PROJ_MIGRATION_DB, description(PROJ_MIGRATION_DB));
    projMigrationDb.setRuntimePeriod(new RuntimePeriod(DATE_7, DATE_6));
    dataHelper.createAVA(projMigrationDb, dateAV(introDate, DATE_2));
    dataHelper.createAVA(projMigrationDb, dateAV(decisionDate, DATE_6));
    dataHelper.createAVA(projMigrationDb, dateAV(evalDate, DATE_9));
  }

  private void createProducts() {
    prodCreditCard = dataHelper.createProduct(PROD_CREDIT_CARD, description(PROD_CREDIT_CARD));
    dataHelper.createAVA(prodCreditCard, healthAvg);
    dataHelper.createAVA(prodCreditCard, numberAV(costs, 10));

    prodLoan = dataHelper.createProduct(PROD_LOAN, description(PROD_LOAN));
    dataHelper.createAVA(prodLoan, healthGood);
    dataHelper.createAVA(prodLoan, numberAV(costs, 55));

    prodSavingsBook = dataHelper.createProduct(PROD_SAVINGS_BOOK, description(PROD_SAVINGS_BOOK));
    dataHelper.createAVA(prodSavingsBook, healthGood);
    dataHelper.createAVA(prodSavingsBook, numberAV(costs, 13));

    prodSpecialAgreement = dataHelper.createProduct(PROD_SPECIAL_AGREEMENT, description(PROD_SPECIAL_AGREEMENT));
    dataHelper.createAVA(prodSpecialAgreement, healthBad);
    dataHelper.createAVA(prodSpecialAgreement, numberAV(costs, 66));
  }

  private void createInfrastructureElements() {

    ieCluster1 = dataHelper.createInfrastructureElement(IE_CLUSTER_1, description(IE_CLUSTER_1));
    dataHelper.createAVA(ieCluster1, healthGood);
    dataHelper.createAVA(ieCluster1, dateAV(introDate, DATE_2));
    dataHelper.createAVA(ieCluster1, dateAV(evalDate, DATE_3));
    dataHelper.createAVA(ieCluster1, dateAV(decisionDate, DATE_5));

    ieCluster2 = dataHelper.createInfrastructureElement(IE_CLUSTER_2, description(IE_CLUSTER_2));
    dataHelper.createAVA(ieCluster2, healthGood);
    dataHelper.createAVA(ieCluster2, numberAV(costs, 33));
    dataHelper.createAVA(ieCluster2, dateAV(introDate, DATE_1));
    dataHelper.createAVA(ieCluster2, dateAV(evalDate, DATE_4));
    dataHelper.createAVA(ieCluster2, dateAV(decisionDate, DATE_6));

    ieIbmHost = dataHelper.createInfrastructureElement(IE_IBM_HOST, description(IE_IBM_HOST));
    dataHelper.createAVA(ieIbmHost, healthAvg);
    dataHelper.createAVA(ieIbmHost, numberAV(costs, 15));
    dataHelper.createAVA(ieIbmHost, dateAV(evalDate, DATE_2));
    dataHelper.createAVA(ieIbmHost, dateAV(decisionDate, DATE_6));

    ieServer760 = dataHelper.createInfrastructureElement(IE_SERVER_760, description(IE_SERVER_760));
    dataHelper.createAVA(ieServer760, numberAV(costs, 8));

    ieServer37s = dataHelper.createInfrastructureElement(IE_SERVER_37S, description(IE_SERVER_37S));
    dataHelper.createAVA(ieServer37s, healthBad);
    dataHelper.createAVA(ieServer37s, numberAV(costs, 4));
    dataHelper.createAVA(ieServer37s, dateAV(introDate, DATE_2));
    dataHelper.createAVA(ieServer37s, dateAV(evalDate, DATE_9));
  }

  private void createBusinessUnits() {
    buControlling = dataHelper.createBusinessUnit(BU_CONTROLLING, description(BU_CONTROLLING));
    dataHelper.createAVA(buControlling, numberAV(costs, 200));
    dataHelper.createAVA(buControlling, dateAV(evalDate, DATE_4));
    dataHelper.createAVA(buControlling, dateAV(decisionDate, DATE_6));

    buCompliance = dataHelper.createBusinessUnit(BU_COMPLIANCE, description(BU_COMPLIANCE));
    dataHelper.createAVA(buCompliance, healthAvg);
    dataHelper.createAVA(buCompliance, numberAV(costs, 20));
    dataHelper.createAVA(buCompliance, dateAV(introDate, DATE_2));
    dataHelper.createAVA(buCompliance, dateAV(evalDate, DATE_6));
    dataHelper.createAVA(buCompliance, dateAV(decisionDate, DATE_9));

    buFinance = dataHelper.createBusinessUnit(BU_FINANCE, description(BU_FINANCE));
    dataHelper.createAVA(buFinance, healthBad);
    dataHelper.createAVA(buFinance, numberAV(costs, 35));
    dataHelper.createAVA(buFinance, dateAV(introDate, DATE_1));
    dataHelper.createAVA(buFinance, dateAV(evalDate, DATE_5));
    dataHelper.createAVA(buFinance, dateAV(decisionDate, DATE_6));

    buItOperations = dataHelper.createBusinessUnit(BU_IT_OPERATIONS, description(BU_IT_OPERATIONS));
    dataHelper.createAVA(buItOperations, healthGood);
    dataHelper.createAVA(buItOperations, numberAV(costs, 16));
    dataHelper.createAVA(buItOperations, dateAV(introDate, DATE_2));
    dataHelper.createAVA(buItOperations, dateAV(evalDate, DATE_4));
  }

  private void createBusinessFunctions() {
    bfBalanceAccount = dataHelper.createBusinessFunction(BF_BALANCE_ACCOUNT, description(BF_BALANCE_ACCOUNT));
    dataHelper.createAVA(bfBalanceAccount, healthGood);
    dataHelper.createAVA(bfBalanceAccount, numberAV(costs, 10));
    dataHelper.createAVA(bfBalanceAccount, dateAV(introDate, DATE_1));
    dataHelper.createAVA(bfBalanceAccount, dateAV(decisionDate, DATE_9));

    bfClearing = dataHelper.createBusinessFunction(BF_CLEARING, description(BF_CLEARING));
    dataHelper.createAVA(bfClearing, healthBad);
    dataHelper.createAVA(bfClearing, numberAV(costs, 22));
    dataHelper.createAVA(bfClearing, dateAV(introDate, DATE_2));
    dataHelper.createAVA(bfClearing, dateAV(evalDate, DATE_3));
    dataHelper.createAVA(bfClearing, dateAV(decisionDate, DATE_4));

    bfDecisionSupport = dataHelper.createBusinessFunction(BF_DECISION_SUPPORT, description(BF_DECISION_SUPPORT));
    dataHelper.createAVA(bfDecisionSupport, healthBad);
    dataHelper.createAVA(bfDecisionSupport, numberAV(costs, 7));
    dataHelper.createAVA(bfDecisionSupport, dateAV(introDate, DATE_1));
    dataHelper.createAVA(bfDecisionSupport, dateAV(evalDate, DATE_2));
    dataHelper.createAVA(bfDecisionSupport, dateAV(decisionDate, DATE_3));

    bfMarketing = dataHelper.createBusinessFunction(BF_MARKETING, description(BF_MARKETING));
    dataHelper.createAVA(bfMarketing, healthAvg);
    dataHelper.createAVA(bfMarketing, numberAV(costs, 95));
    dataHelper.createAVA(bfMarketing, dateAV(introDate, DATE_1));
    dataHelper.createAVA(bfMarketing, dateAV(evalDate, DATE_6));
    dataHelper.createAVA(bfMarketing, dateAV(decisionDate, DATE_9));

    bfStrategicDev = dataHelper.createBusinessFunction(BF_STRATEGIC_DEV, description(BF_STRATEGIC_DEV));
    dataHelper.createAVA(bfStrategicDev, healthGood);
    dataHelper.createAVA(bfStrategicDev, numberAV(costs, 45));
    dataHelper.createAVA(bfStrategicDev, dateAV(introDate, DATE_3));
    dataHelper.createAVA(bfStrategicDev, dateAV(decisionDate, DATE_6));

    bfTxHandling = dataHelper.createBusinessFunction(BF_TX_HANDLING, description(BF_TX_HANDLING));
    dataHelper.createAVA(bfTxHandling, healthAvg);
    dataHelper.createAVA(bfTxHandling, numberAV(costs, 32));
    dataHelper.createAVA(bfTxHandling, dateAV(introDate, DATE_2));
    dataHelper.createAVA(bfTxHandling, dateAV(evalDate, DATE_4));
    dataHelper.createAVA(bfTxHandling, dateAV(decisionDate, DATE_6));
  }

  private void createBusinessProcesses() {
    //just a single one for now
    bpMarketing = dataHelper.createBusinessProcess(BP_MARKETING, description(BP_MARKETING));
  }

  private void createIsrSelfRelationships() {
    //successors
    dataHelper.addSuccessorToIsr(isCrm31, isCrm32);

    //hierarchy
    dataHelper.addChildToIsr(isSap, isSapFi);
    dataHelper.addChildToIsr(isSap, isSapPi);

    //usage
    dataHelper.addBaseComponentToISRelease(isCrm31, isDwh);
    dataHelper.addBaseComponentToISRelease(isCrm32, isDwh);
    dataHelper.addBaseComponentToISRelease(isElBanking, isDwh);
    dataHelper.addBaseComponentToISRelease(isFunds, isDwh);
    dataHelper.addBaseComponentToISRelease(isDms, isDwh);
    dataHelper.addBaseComponentToISRelease(isCrm31, isElBanking);
    dataHelper.addBaseComponentToISRelease(isCrm32, isElBanking);
    dataHelper.addBaseComponentToISRelease(isFunds, isElBanking);
    dataHelper.addBaseComponentToISRelease(isSap, isFunds);
    dataHelper.addBaseComponentToISRelease(isScm, isFunds);
    dataHelper.addBaseComponentToISRelease(isSapPi, isFunds);
  }

  private void createTcrSelfRelationships() {
    //successors
    dataHelper.addSuccessorToTCRelease(tcrWin2k, tcrWin7);
    dataHelper.addSuccessorToTCRelease(tcrJava14, tcrJava5);
    dataHelper.addSuccessorToTCRelease(tcrJava14, tcrJava6);
    dataHelper.addSuccessorToTCRelease(tcrJava5, tcrJava6);

    //usage (semantically not ok...)
    dataHelper.addBaseComponentToTCRelease(tcrJava14, tcrJava);
    dataHelper.addBaseComponentToTCRelease(tcrJava5, tcrJava);
    dataHelper.addBaseComponentToTCRelease(tcrJava6, tcrJava);
    dataHelper.addBaseComponentToTCRelease(tcrWin2k, tcrWin);
    dataHelper.addBaseComponentToTCRelease(tcrWin7, tcrWin);
  }

  private void createBusinessMappings() {
    // isCrm31
    dataHelper.createBusinessMapping(isCrm31, bpMarketing, buControlling, prodCreditCard);
    dataHelper.createBusinessMapping(isCrm31, bpMarketing, buControlling, prodLoan);
    dataHelper.createBusinessMapping(isCrm31, bpMarketing, buControlling, prodSavingsBook);
    dataHelper.createBusinessMapping(isCrm31, bpMarketing, buCompliance, prodSavingsBook);
    dataHelper.createBusinessMapping(isCrm31, bpMarketing, buCompliance, prodSpecialAgreement);
    dataHelper.createBusinessMapping(isCrm31, bpMarketing, buFinance, prodCreditCard);
    dataHelper.createBusinessMapping(isCrm31, bpMarketing, buItOperations, prodSpecialAgreement);

    // isCrm32
    dataHelper.createBusinessMapping(isCrm32, bpMarketing, buCompliance, prodCreditCard);
    dataHelper.createBusinessMapping(isCrm32, bpMarketing, buCompliance, prodLoan);
    dataHelper.createBusinessMapping(isCrm32, bpMarketing, buCompliance, prodSavingsBook);
    dataHelper.createBusinessMapping(isCrm32, bpMarketing, buCompliance, prodSpecialAgreement);
    dataHelper.createBusinessMapping(isCrm32, bpMarketing, buControlling, prodLoan);
    dataHelper.createBusinessMapping(isCrm32, bpMarketing, buControlling, prodSavingsBook);
    dataHelper.createBusinessMapping(isCrm32, bpMarketing, buFinance, prodSavingsBook);
    dataHelper.createBusinessMapping(isCrm32, bpMarketing, buFinance, prodSpecialAgreement);
    dataHelper.createBusinessMapping(isCrm32, bpMarketing, buFinance, prodLoan);
    dataHelper.createBusinessMapping(isCrm32, bpMarketing, buItOperations, prodSpecialAgreement);

    // isSap
    dataHelper.createBusinessMapping(isSap, bpMarketing, buFinance, prodCreditCard);
    dataHelper.createBusinessMapping(isSap, bpMarketing, buFinance, prodLoan);
    dataHelper.createBusinessMapping(isSap, bpMarketing, buItOperations, prodLoan);
    dataHelper.createBusinessMapping(isSap, bpMarketing, buItOperations, prodSpecialAgreement);
    dataHelper.createBusinessMapping(isSap, bpMarketing, buItOperations, prodSavingsBook);

    // isSapPi
    dataHelper.createBusinessMapping(isSapPi, bpMarketing, buCompliance, prodLoan);
    dataHelper.createBusinessMapping(isSapPi, bpMarketing, buCompliance, prodSavingsBook);
    dataHelper.createBusinessMapping(isSapPi, bpMarketing, buControlling, prodSpecialAgreement);
    dataHelper.createBusinessMapping(isSapPi, bpMarketing, buControlling, prodSavingsBook);
    dataHelper.createBusinessMapping(isSapPi, bpMarketing, buControlling, prodCreditCard);
    dataHelper.createBusinessMapping(isSapPi, bpMarketing, buFinance, prodCreditCard);
    dataHelper.createBusinessMapping(isSapPi, bpMarketing, buFinance, prodSavingsBook);

    // isSapFi
    dataHelper.createBusinessMapping(isSapFi, bpMarketing, buFinance, prodCreditCard);
    dataHelper.createBusinessMapping(isSapFi, bpMarketing, buFinance, prodSavingsBook);
    dataHelper.createBusinessMapping(isSapFi, bpMarketing, buFinance, prodSpecialAgreement);
    dataHelper.createBusinessMapping(isSapFi, bpMarketing, buItOperations, prodLoan);
    dataHelper.createBusinessMapping(isSapFi, bpMarketing, buItOperations, prodCreditCard);
    dataHelper.createBusinessMapping(isSapFi, bpMarketing, buItOperations, prodSavingsBook);
    dataHelper.createBusinessMapping(isSapFi, bpMarketing, buItOperations, prodSpecialAgreement);

    // isFunds
    dataHelper.createBusinessMapping(isFunds, bpMarketing, buFinance, prodLoan);
    dataHelper.createBusinessMapping(isFunds, bpMarketing, buFinance, prodCreditCard);
    dataHelper.createBusinessMapping(isFunds, bpMarketing, buItOperations, prodSpecialAgreement);

    // isDms
    dataHelper.createBusinessMapping(isDms, bpMarketing, buCompliance, prodSpecialAgreement);
    dataHelper.createBusinessMapping(isDms, bpMarketing, buCompliance, prodSavingsBook);
    dataHelper.createBusinessMapping(isDms, bpMarketing, buControlling, prodSpecialAgreement);

    // isDwh
    dataHelper.createBusinessMapping(isDwh, bpMarketing, buControlling, prodSpecialAgreement);
    dataHelper.createBusinessMapping(isDwh, bpMarketing, buControlling, prodLoan);

    // isElBanking
    dataHelper.createBusinessMapping(isElBanking, bpMarketing, buItOperations, prodCreditCard);
    dataHelper.createBusinessMapping(isElBanking, bpMarketing, buItOperations, prodLoan);
    dataHelper.createBusinessMapping(isElBanking, bpMarketing, buItOperations, prodSavingsBook);
    dataHelper.createBusinessMapping(isElBanking, bpMarketing, buItOperations, prodSpecialAgreement);
    dataHelper.createBusinessMapping(isElBanking, bpMarketing, buCompliance, prodCreditCard);
    dataHelper.createBusinessMapping(isElBanking, bpMarketing, buCompliance, prodLoan);
    dataHelper.createBusinessMapping(isElBanking, bpMarketing, buCompliance, prodSpecialAgreement);

    // isScm
    dataHelper.createBusinessMapping(isScm, bpMarketing, buControlling, prodCreditCard);
    dataHelper.createBusinessMapping(isScm, bpMarketing, buCompliance, prodLoan);
    dataHelper.createBusinessMapping(isScm, bpMarketing, buFinance, prodLoan);
    dataHelper.createBusinessMapping(isScm, bpMarketing, buFinance, prodSavingsBook);
    dataHelper.createBusinessMapping(isScm, bpMarketing, buItOperations, prodSpecialAgreement);
  }

  //
  //  private void createBusinessMappingsToBusinessFunctions() {
  //
  //    // isCrm31
  //    dataHelper.createBusinessMappingToFunction(isCrm31, bfBalanceAccount);
  //    dataHelper.createBusinessMappingToFunction(isCrm31, bfClearing);
  //    dataHelper.createBusinessMappingToFunction(isCrm31, bfMarketing);
  //    dataHelper.createBusinessMappingToFunction(isCrm31, bfDecisionSupport);
  //
  //    // isCrm32
  //    dataHelper.createBusinessMappingToFunction(isCrm32, bfDecisionSupport);
  //    dataHelper.createBusinessMappingToFunction(isCrm32, bfMarketing);
  //    dataHelper.createBusinessMappingToFunction(isCrm32, bfStrategicDev);
  //    dataHelper.createBusinessMappingToFunction(isCrm32, bfTxHandling);
  //    dataHelper.createBusinessMappingToFunction(isCrm32, bfClearing);
  //
  //    // isSap
  //    dataHelper.createBusinessMappingToFunction(isSap, bfStrategicDev);
  //    dataHelper.createBusinessMappingToFunction(isSap, bfTxHandling);
  //    dataHelper.createBusinessMappingToFunction(isSap, bfDecisionSupport);
  //    dataHelper.createBusinessMappingToFunction(isSap, bfMarketing);
  //
  //    // isSapPi
  //    dataHelper.createBusinessMappingToFunction(isSapPi, bfStrategicDev);
  //    dataHelper.createBusinessMappingToFunction(isSapPi, bfDecisionSupport);
  //    dataHelper.createBusinessMappingToFunction(isSapPi, bfMarketing);
  //
  //    // isSapFi
  //    dataHelper.createBusinessMappingToFunction(isSapFi, bfStrategicDev);
  //    dataHelper.createBusinessMappingToFunction(isSapFi, bfTxHandling);
  //    dataHelper.createBusinessMappingToFunction(isSapFi, bfMarketing);
  //    dataHelper.createBusinessMappingToFunction(isSapFi, bfDecisionSupport);
  //
  //    // isFunds
  //    dataHelper.createBusinessMappingToFunction(isFunds, bfBalanceAccount);
  //    dataHelper.createBusinessMappingToFunction(isFunds, bfClearing);
  //    dataHelper.createBusinessMappingToFunction(isFunds, bfMarketing);
  //
  //    // isDms
  //    dataHelper.createBusinessMappingToFunction(isDms, bfDecisionSupport);
  //    dataHelper.createBusinessMappingToFunction(isDms, bfStrategicDev);
  //
  //    // isElBanking
  //    dataHelper.createBusinessMappingToFunction(isElBanking, bfBalanceAccount);
  //    dataHelper.createBusinessMappingToFunction(isElBanking, bfStrategicDev);
  //    dataHelper.createBusinessMappingToFunction(isElBanking, bfClearing);
  //  }

  private void createTcr2IeAssociations() {

    // tcrWin2k
    dataHelper.addIeToTcr(tcrWin2k, ieCluster1);
    dataHelper.addIeToTcr(tcrWin2k, ieIbmHost);
    dataHelper.addIeToTcr(tcrWin2k, ieServer37s);
    dataHelper.addIeToTcr(tcrWin2k, ieServer760);

    // tcrWin7
    dataHelper.addIeToTcr(tcrWin2k, ieCluster1);
    dataHelper.addIeToTcr(tcrWin2k, ieCluster2);
    dataHelper.addIeToTcr(tcrWin2k, ieIbmHost);

    // tcrJava14
    dataHelper.addIeToTcr(tcrWin2k, ieServer37s);

    // tcrJava5
    dataHelper.addIeToTcr(tcrWin2k, ieCluster1);
    dataHelper.addIeToTcr(tcrWin2k, ieServer37s);
    dataHelper.addIeToTcr(tcrWin2k, ieServer760);
    dataHelper.addIeToTcr(tcrWin2k, ieIbmHost);

    // tcrJava6
    dataHelper.addIeToTcr(tcrWin2k, ieIbmHost);
    dataHelper.addIeToTcr(tcrWin2k, ieServer760);

    // tcrOracle
    dataHelper.addIeToTcr(tcrWin2k, ieCluster1);
    dataHelper.addIeToTcr(tcrWin2k, ieCluster2);
    dataHelper.addIeToTcr(tcrWin2k, ieIbmHost);
    dataHelper.addIeToTcr(tcrWin2k, ieServer37s);
    dataHelper.addIeToTcr(tcrWin2k, ieServer760);
  }

  private void connectAd2Tc() {
    dataHelper.addADToTCRelease(tcrWin, adOperatingSystem);
    dataHelper.addADToTCRelease(tcrWin2k, adOperatingSystem);
    dataHelper.addADToTCRelease(tcrWin7, adOperatingSystem);

    dataHelper.addADToTCRelease(tcrJava, adProgrammingLanguage);
    dataHelper.addADToTCRelease(tcrJava14, adProgrammingLanguage);
    dataHelper.addADToTCRelease(tcrJava5, adProgrammingLanguage);
    dataHelper.addADToTCRelease(tcrJava6, adProgrammingLanguage);

    dataHelper.addADToTCRelease(tcrOracle, adDatabase);
  }

  private void connectTc2Isr() {
    dataHelper.addTcrToIsr(isFunds, tcrWin2k);
    dataHelper.addTcrToIsr(isFunds, tcrWin7);
    dataHelper.addTcrToIsr(isFunds, tcrOracle);
    dataHelper.addTcrToIsr(isCrm31, tcrOracle);
    dataHelper.addTcrToIsr(isCrm32, tcrOracle);
    dataHelper.addTcrToIsr(isSap, tcrOracle);
    dataHelper.addTcrToIsr(isSapFi, tcrOracle);
    dataHelper.addTcrToIsr(isSapPi, tcrOracle);
    dataHelper.addTcrToIsr(isCrm31, tcrJava14);
    dataHelper.addTcrToIsr(isCrm32, tcrJava5);
    dataHelper.addTcrToIsr(isDms, tcrJava5);
    dataHelper.addTcrToIsr(isElBanking, tcrJava5);
    dataHelper.addTcrToIsr(isElBanking, tcrOracle);
    dataHelper.addTcrToIsr(isCrm31, tcrWin);
    dataHelper.addTcrToIsr(isCrm32, tcrWin);
    dataHelper.addTcrToIsr(isDwh, tcrWin);
    dataHelper.addTcrToIsr(isDms, tcrWin);
  }

  private void connectProj2Isr() {
    // isCrm31
    dataHelper.addIsrToProject(isCrm31, projCentralization);
    dataHelper.addIsrToProject(isCrm31, projConsolidation);
    dataHelper.addIsrToProject(isCrm31, projMigrationDb);

    // isCrm32
    dataHelper.addIsrToProject(isCrm32, projCentralization);
    dataHelper.addIsrToProject(isCrm32, projConsolidation);
    dataHelper.addIsrToProject(isCrm32, projDbBenchmark);
    dataHelper.addIsrToProject(isCrm32, projCrm);

    // isSap
    dataHelper.addIsrToProject(isSap, projCentralization);
    dataHelper.addIsrToProject(isSap, projDbBenchmark);

    // isSapPi
    dataHelper.addIsrToProject(isSapPi, projConsolidation);
    dataHelper.addIsrToProject(isSapPi, projCrm);
    dataHelper.addIsrToProject(isSapPi, projMigrationDb);

    // isSapFi
    dataHelper.addIsrToProject(isSapFi, projConsolidation);
    dataHelper.addIsrToProject(isSapFi, projMigrationDb);

    // isFunds
    dataHelper.addIsrToProject(isFunds, projCentralization);

    // isDms
    dataHelper.addIsrToProject(isDms, projMigrationDb);
    dataHelper.addIsrToProject(isDms, projDbBenchmark);

    // isScm
    dataHelper.addIsrToProject(isScm, projConsolidation);
    dataHelper.addIsrToProject(isScm, projCrm);
  }

  private void connectBf2IIsr() {
    //isCrm31
    dataHelper.addBfToIsr(isCrm31, bfBalanceAccount);
    dataHelper.addBfToIsr(isCrm31, bfClearing);
    dataHelper.addBfToIsr(isCrm31, bfMarketing);
    dataHelper.addBfToIsr(isCrm31, bfDecisionSupport);

    // isCrm32
    dataHelper.addBfToIsr(isCrm32, bfDecisionSupport);
    dataHelper.addBfToIsr(isCrm32, bfMarketing);
    dataHelper.addBfToIsr(isCrm32, bfStrategicDev);
    dataHelper.addBfToIsr(isCrm32, bfTxHandling);
    dataHelper.addBfToIsr(isCrm32, bfClearing);

    // isSap
    dataHelper.addBfToIsr(isSap, bfStrategicDev);
    dataHelper.addBfToIsr(isSap, bfTxHandling);
    dataHelper.addBfToIsr(isSap, bfDecisionSupport);
    dataHelper.addBfToIsr(isSap, bfMarketing);

    // isSapPi
    dataHelper.addBfToIsr(isSapPi, bfStrategicDev);
    dataHelper.addBfToIsr(isSapPi, bfDecisionSupport);
    dataHelper.addBfToIsr(isSapPi, bfMarketing);

    // isSapFi
    dataHelper.addBfToIsr(isSapFi, bfStrategicDev);
    dataHelper.addBfToIsr(isSapFi, bfTxHandling);
    dataHelper.addBfToIsr(isSapFi, bfMarketing);
    dataHelper.addBfToIsr(isSapFi, bfDecisionSupport);

    // isFunds
    dataHelper.addBfToIsr(isFunds, bfBalanceAccount);
    dataHelper.addBfToIsr(isFunds, bfClearing);
    dataHelper.addBfToIsr(isFunds, bfMarketing);

    // isDms
    dataHelper.addBfToIsr(isDms, bfDecisionSupport);
    dataHelper.addBfToIsr(isDms, bfStrategicDev);

    // isElBanking
    dataHelper.addBfToIsr(isElBanking, bfBalanceAccount);
    dataHelper.addBfToIsr(isElBanking, bfStrategicDev);
    dataHelper.addBfToIsr(isElBanking, bfClearing);
  }

  private void connectIsr2Ie() {
    // isCrm31
    dataHelper.addIeToIsr(isCrm31, ieCluster1);
    dataHelper.addIeToIsr(isCrm31, ieCluster2);
    dataHelper.addIeToIsr(isCrm31, ieServer37s);

    // isCrm32
    dataHelper.addIeToIsr(isCrm32, ieCluster1);
    dataHelper.addIeToIsr(isCrm32, ieCluster2);

    // isSap
    dataHelper.addIeToIsr(isSap, ieIbmHost);

    // isSapPi
    dataHelper.addIeToIsr(isSapPi, ieIbmHost);
    dataHelper.addIeToIsr(isSapPi, ieCluster2);

    // isSapFi
    dataHelper.addIeToIsr(isSapFi, ieServer37s);
    dataHelper.addIeToIsr(isSapFi, ieServer760);
    dataHelper.addIeToIsr(isSapFi, ieCluster2);

    // isFunds
    dataHelper.addIeToIsr(isFunds, ieIbmHost);

    // isDms
    dataHelper.addIeToIsr(isDms, ieIbmHost);
    dataHelper.addIeToIsr(isDms, ieServer760);

    // isElBanking
    dataHelper.addIeToIsr(isElBanking, ieServer37s);
    dataHelper.addIeToIsr(isElBanking, ieServer760);
    dataHelper.addIeToIsr(isElBanking, ieCluster1);
    dataHelper.addIeToIsr(isElBanking, ieIbmHost);

    // isScm
    dataHelper.addIeToIsr(isScm, ieIbmHost);
    dataHelper.addIeToIsr(isScm, ieServer760);
  }

  private NumberAV numberAV(NumberAT numberAT, double value) {
    return dataHelper.createNumberAV(BigDecimal.valueOf(value), numberAT);
  }

  private DateAV dateAV(DateAT dateAT, Date value) {
    return dataHelper.createDateAV(value, dateAT);
  }

  private static String description(String name) {
    return name + " Description";
  }

  public static Date parseDate(String dateString) {
    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    try {
      return dateFormat.parse(dateString);
    } catch (ParseException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

}
