package org.diegor.junit5.ejemplos.models;

import org.diegor.junit5.ejemplos.exception.MoneyInsuficientException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

class CountTest {
    private Count count;
    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeAll
    static void beforeAll() {
        System.out.println("initiating test");
    }

    @BeforeEach
    void setUp(TestInfo testInfo, TestReporter testReporter) {
        System.out.println("initiating method");
        this.count = new Count("Diego", new BigDecimal("1000.20"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("tst info"+ testInfo.getDisplayName());
        testReporter.publishEntry("executing:" + testInfo.getDisplayName() + " " + testInfo.getTestMethod().get().getName()
        + " " + "with tags: " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("finishing method");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("finishing test");
    }

    @Tag("count")
    @Nested
    @DisplayName("Testing Account properties and Mount quantities from an account")
    class CountNameAndMountTest {

        @Test
        @Disabled
        @DisplayName("actual count name test")
        void testCountName(){
            Assertions.fail();
            String expected = "Diego";
            String actual = count.getPersona();
            Assertions.assertEquals(expected,actual);
            Assertions.assertEquals("Diego", actual,()-> "name it has be diego");
        }

        @Test
        @DisplayName("mount count test")
        void testMountCount() {
            testReporter.publishEntry("hello");
            testReporter.publishEntry("hello");
            Assertions.assertEquals(1000.20, count.getMoney().doubleValue());
            Assertions.assertFalse(count.getMoney().compareTo(BigDecimal.ZERO) < 0);
            Assertions.assertTrue(count.getMoney().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("comparing reference of two counts")
        void testCountReference() {
            count = new Count("Jhon Doe", new BigDecimal("100.20"));
            Count count2 = new Count("Jhon Doe", new BigDecimal("100.20"));
            Assertions.assertEquals(count, count2);
        }
    }

    @Nested
    class proofCountTest{

        @Tag("count")
        @Test
        void testDebitCount() {
            count.debit(new BigDecimal(100));
            Assertions.assertNotNull(count.getMoney());
            Assertions.assertEquals(900, count.getMoney().intValue());
            Assertions.assertEquals("900.20" , count.getMoney().toPlainString());
        }

        @Tag("count")
        @Test
        void testCreditCount() {
            count.credit(new BigDecimal(100));
            Assertions.assertNotNull(count.getMoney());
            Assertions.assertEquals(1100, count.getMoney().intValue());
            Assertions.assertEquals("1100.20" , count.getMoney().toPlainString());
        }

        @Tag("bank")
        @Test
        void testMoneyInsufficientTest() {
            Exception exception = Assertions.assertThrows(MoneyInsuficientException.class,()->
                    count.debit(new BigDecimal("3000")));
            String actual = exception.getMessage();
            String expected = "Insufficient Money";
            Assertions.assertEquals(expected, actual);
        }

        @Tag("count")
        @Test
        void countTransferTest() {
            Count countTwo = new Count( "Alejandro", new BigDecimal("2000"));
            Banco banco = new Banco();
            banco.setName("Mexico bank");
            banco.transfer(count, countTwo, new BigDecimal("1000"));
            Assertions.assertEquals("3000", countTwo.getMoney().toPlainString());

        }

        @Test
        void testBankCountRelation() {
            Count countTwo = new Count( "Alejandro", new BigDecimal("2000"));
            Banco banco = new Banco();
            banco.addCount(count);
            banco.addCount(countTwo);
            banco.setName("Mexico bank");
            banco.transfer(count, countTwo, new BigDecimal("1000"));
            Assertions.assertEquals(2, banco.getCountList().size(),() -> "El numero de cuentas debe de ser de 3");
            Assertions.assertEquals("Mexico bank" , count.getBank().getName(),()-> "El nombre del banco debe de ser Mexico bank");
            Assertions.assertEquals("Diego", banco.getCountList().stream()
                    .filter(d -> d.getPersona().equals("Diego"))
                    .findFirst().get().getPersona());
            Assertions.assertTrue(banco.getCountList().stream().anyMatch(d -> d.getPersona().equals("Diego")));

        }
    }


    @Nested
    class OperativeAndJavaVersionSystemTest{

        @Test
        @EnabledOnOs(OS.WINDOWS)
        void windowsOnly(){

        }

        @Test
        @EnabledOnOs({ OS.LINUX, OS.MAC})
        void linuxMacOnly(){

        }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void testOnJava11() {
        }
    }


    @Nested
    class PropertiesAndEnvironmentVariablesTest{
        @Test
        void propertiesPrinter(){
            Properties properties = System.getProperties();
            properties.forEach((key,value)-> System.out.println(key+ " :"+value));
        }

        @Test
        void environmentVariablesPrinter() {
            Map<String, String > map = System.getenv();
            map.forEach((key, value) -> System.out.println(key + " :" +value));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-11.0.14.*")
        void testJavaHome(){
        }
    }


    @Test
    @DisplayName("mount count test in java 11 and assume")
    void testMountCountInProd() {
        Properties properties = System.getProperties();
        Optional javaVersion = Optional.ofNullable(properties.get("java.version"));
        if(javaVersion.isPresent())
            System.out.println(javaVersion.get().toString());
        else javaVersion.orElse("java version not supported");
        Assumptions.assumingThat(javaVersion.get().toString().matches("^11\\..*"),
        ()-> {
            Assertions.assertEquals(1000.20, count.getMoney().doubleValue());
            Assertions.assertFalse(count.getMoney().compareTo(BigDecimal.ZERO) < 0);
            Assertions.assertTrue(count.getMoney().compareTo(BigDecimal.ZERO) > 0);
        });


    }


    @Test
    @DisplayName("mount count test")
    void testMountCountInJava11() {
        boolean prod = "ENVIRONMENT".equals(Optional.ofNullable(System.getenv("ENVIRONMENT"))
                .orElse("environment"));
        Assumptions.assumeFalse(prod);
        Assertions.assertEquals(1000.20, count.getMoney().doubleValue());
        Assertions.assertFalse(count.getMoney().compareTo(BigDecimal.ZERO) < 0);
        Assertions.assertTrue(count.getMoney().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Repetition test")
    @RepeatedTest(value = 5, name = "{displayName} repetition number {currentRepetition}")
    void testMethod (RepetitionInfo repetitionInfo) {
    Assumptions.assumingThat(repetitionInfo.getCurrentRepetition() == 2, ()-> {
        System.out.println("hello");
    });

        System.out.println("hello world");
    }

    @Tag("parameterized")
    @Nested
    class parameterizedTests{

        @ParameterizedTest(name = "number repetition {index} value {argumentsWithNames}")
        @ValueSource(strings = {"100", "500", "1000", "50000"})
        void debitCountTestValueSource(String mount){
            count.debit(new BigDecimal(mount));
            Assertions.assertTrue(count.getMoney().compareTo(BigDecimal.ZERO) > 0 , ()-> "insufficient money");
        }

        @ParameterizedTest(name = "number repetition {index} value {argumentsWithNames}")
        @CsvSource({"1,100", "2,500", "3,1000", "4,50000"})
        void debitCountTestCsvSource(String index , String mount){
            count.debit(new BigDecimal(mount));
            Assertions.assertTrue(count.getMoney().compareTo(BigDecimal.ZERO) > 0 ,()-> "Insufficient money");
        }

        @ParameterizedTest(name = "number repetition {index} value {argumentsWithNames}")
        @CsvSource({"200,100,Diego,Diego", "677,500,Jose,Jose", "1000,1000,Maria,maria", "230,50000,hector,hector"})
        void debitCountTestCsvSourceTwo(String countMoney , String mount,String actual, String expected){
            count.setMoney(new BigDecimal(countMoney));
            count.debit(new BigDecimal(mount));
            Assertions.assertEquals(expected, actual );
            Assertions.assertTrue(count.getMoney().compareTo(BigDecimal.ZERO) > 0 ,()-> "Insufficient money");
        }

        @ParameterizedTest(name = "number repetition {index} value {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void debitCountTestCsvFileSource(String mount){
            count.debit(new BigDecimal(mount));
            Assertions.assertTrue(count.getMoney().compareTo(BigDecimal.ZERO) > 0 ,()-> "Insufficient money");
        }

        @ParameterizedTest(name = "number repetition {index} value {argumentWithNames}")
        @CsvFileSource(resources = "/dataTwo.csv")
        void debitCountTestCsvFileSourceTwo(String countMoney , String mount,String actual, String expected){
            count.setMoney(new BigDecimal(countMoney));
            count.debit(new BigDecimal(mount));
            count.setPersona(actual);

            Assertions.assertNotNull(count.getPersona());
            Assertions.assertEquals(expected, actual );
            Assertions.assertTrue(count.getMoney().compareTo(BigDecimal.ZERO) > 0 ,()-> "Insufficient money");
        }

    }

    @ParameterizedTest(name = "number repetition {index} value {argumentsWithNames}")
    @MethodSource("mountList")
    void debitCountTestMethodSource(String mount){
        count.debit(new BigDecimal(mount));
        Assertions.assertTrue(count.getMoney().compareTo(BigDecimal.ZERO) > 0 ,()-> "Insufficient money");
    }

    static List<String> mountList () {
        return Arrays.asList("100", "500", "1000", "50000");
    }


    @Nested
    @Tag("timeOut")
    class TimeOutExample{

        @Test
        @Timeout(5)
        void timeOutTest() throws InterruptedException {
            testReporter.publishEntry("hello");
            TimeUnit.SECONDS.sleep(6);
        }

        @Test
        @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
        void timeOutTestTwo() throws InterruptedException {
            TimeUnit.SECONDS.sleep(5);
        }

        @Test
        void timeOutAssertions() {
            Assertions.assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.SECONDS.sleep(3);
            });
        }
    }

}