package se.fk.rimfrost.framework.oul.adapter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;

import io.quarkus.test.InjectMock;
import io.quarkus.test.component.QuarkusComponentTest;
import jakarta.inject.Inject;
import se.fk.rimfrost.framework.oul.exception.OulException;
import se.fk.rimfrost.framework.oul.model.CreateOperativUppgiftRequest;
import se.fk.rimfrost.framework.oul.model.ImmutableCreateOperativUppgiftRequest;
import se.fk.rimfrost.framework.oul.model.ImmutableIdtyp;
import se.fk.rimfrost.framework.oul.model.ImmutableOperativUppgift;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@QuarkusComponentTest
public class OulAdapterTest
{

   private static WireMockServer server;

   private static final UUID HANDLAGGNING_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
   private static final UUID UPPGIFT_ID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");

   @Inject
   OulAdapter oulAdapter;

   @InjectMock
   OulMapper oulMapper;

   @BeforeAll
   public static void setup()
   {
      server = new WireMockServer(
            options()
                  .dynamicPort()
                  .usingFilesUnderDirectory("src/test/resources"));
      server.start();

      System.setProperty("oul.api.base-url", server.baseUrl());
   }

   @AfterAll
   public static void teardown()
   {
      server.stop();
   }

   @BeforeEach
   void resetStubs()
   {
      server.resetToDefaultMappings();
   }

   // createOperativUppgift tests

   @Test
   void createUppgiftThrowsBadRequest()
   {
      server.stubFor(WireMock.post(WireMock.urlPathEqualTo("/uppgifter"))
            .willReturn(WireMock.aResponse().withStatus(400)));

      var exception = assertThrows(OulException.class, () -> oulAdapter.createOperativUppgift(createRequest()));
      assertEquals(OulException.ErrorType.BAD_REQUEST, exception.getErrorType());
   }

   @Test
   void createUppgiftThrowsServiceUnavailable()
   {
      server.stubFor(WireMock.post(WireMock.urlPathEqualTo("/uppgifter"))
            .willReturn(WireMock.aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

      var exception = assertThrows(OulException.class, () -> oulAdapter.createOperativUppgift(createRequest()));
      assertEquals(OulException.ErrorType.SERVICE_UNAVAILABLE, exception.getErrorType());
   }

   @Test
   void createUppgiftThrowsUnexpectedError()
   {
      server.stubFor(WireMock.post(WireMock.urlPathEqualTo("/uppgifter"))
            .willReturn(WireMock.aResponse().withStatus(500)));

      var exception = assertThrows(OulException.class, () -> oulAdapter.createOperativUppgift(createRequest()));
      assertEquals(OulException.ErrorType.UNEXPECTED_ERROR, exception.getErrorType());
   }

   @Test
   void createUppgiftReturnsOperativUppgift() throws OulException
   {

      var expected = operativUppgift();
      Mockito.when(oulMapper.toOperativUppgift(any())).thenReturn(expected);

      var result = oulAdapter.createOperativUppgift(createRequest());

      assertNotNull(result);
      assertEquals(expected, result);
   }

   @Test
   void endUppgiftThrowsNotFound()
   {
      server.stubFor(WireMock.post(WireMock.urlPathMatching("/uppgifter/[^/]+/end"))
            .willReturn(WireMock.aResponse().withStatus(404)));

      var exception = assertThrows(OulException.class, () -> oulAdapter.endOperativUppgift(UPPGIFT_ID, "test-reason"));
      assertEquals(OulException.ErrorType.NOT_FOUND, exception.getErrorType());
   }

   @Test
   void endUppgiftThrowsBadRequest()
   {
      server.stubFor(WireMock.post(WireMock.urlPathMatching("/uppgifter/[^/]+/end"))
            .willReturn(WireMock.aResponse().withStatus(400)));

      var exception = assertThrows(OulException.class, () -> oulAdapter.endOperativUppgift(UPPGIFT_ID, "test-reason"));
      assertEquals(OulException.ErrorType.BAD_REQUEST, exception.getErrorType());
   }

   @Test
   void endUppgiftThrowsServiceUnavailable()
   {
      server.stubFor(WireMock.post(WireMock.urlPathMatching("/uppgifter/[^/]+/end"))
            .willReturn(WireMock.aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

      var exception = assertThrows(OulException.class, () -> oulAdapter.endOperativUppgift(UPPGIFT_ID, "test-reason"));
      assertEquals(OulException.ErrorType.SERVICE_UNAVAILABLE, exception.getErrorType());
   }

   @Test
   void endUppgiftThrowsUnexpectedError()
   {
      server.stubFor(WireMock.post(WireMock.urlPathMatching("/uppgifter/[^/]+/end"))
            .willReturn(WireMock.aResponse().withStatus(500)));

      var exception = assertThrows(OulException.class, () -> oulAdapter.endOperativUppgift(UPPGIFT_ID, "test-reason"));
      assertEquals(OulException.ErrorType.UNEXPECTED_ERROR, exception.getErrorType());
   }

   @Test
   void endUppgiftReturnsOperativUppgift() throws OulException
   {

      var expected = operativUppgift();
      Mockito.when(oulMapper.toOperativUppgift(any())).thenReturn(expected);

      var result = oulAdapter.endOperativUppgift(UPPGIFT_ID, "test-reason");

      assertNotNull(result);
      assertEquals(expected, result);
   }

   // helpers

   private static CreateOperativUppgiftRequest createRequest()
   {
      return ImmutableCreateOperativUppgiftRequest.builder()
            .version("1.0")
            .handlaggningId(HANDLAGGNING_ID)
            .individer(List.of(ImmutableIdtyp.builder()
                  .typId("PERSONNUMMER")
                  .varde("197001011234")
                  .build()))
            .regel("TEST_REGEL")
            .beskrivning("Testbeskrivning")
            .verksamhetslogik("TEST_VERKSAMHETSLOGIK")
            .roll("HANDLAGGARE")
            .url("test.com/uppgifter")
            .subTopic("test-subtopic")
            .cloudeventAttributes(Map.of("source", "test-source", "type", "test-type"))
            .build();
   }

   private static ImmutableOperativUppgift operativUppgift()
   {
      return ImmutableOperativUppgift.builder()
            .uppgiftId(UPPGIFT_ID)
            .handlaggningId(HANDLAGGNING_ID)
            .status("ACTIVE")
            .cloudeventAttributes(Map.of("source", "test-source"))
            .build();
   }
}
