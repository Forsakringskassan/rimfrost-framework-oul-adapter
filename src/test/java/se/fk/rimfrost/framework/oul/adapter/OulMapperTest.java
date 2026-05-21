package se.fk.rimfrost.framework.oul.adapter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se.fk.rimfrost.framework.oul.model.CreateOperativUppgiftRequest;
import se.fk.rimfrost.framework.oul.model.ImmutableCreateOperativUppgiftRequest;
import se.fk.rimfrost.framework.oul.model.ImmutableErbjudande;
import se.fk.rimfrost.framework.oul.model.ImmutableIdtyp;
import se.fk.rimfrost.oul.management.jaxrsspec.controllers.generatedsource.model.UppgiftResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OulMapperTest
{

   private static final UUID HANDLAGGNING_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
   private static final UUID UPPGIFT_ID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");

   OulMapper mapper;

   @BeforeEach
   public void beforeEach()
   {
      mapper = new OulMapper();
   }

   @Test
   void toCreateUppgiftRequestMapsAllFields()
   {
      var createOperativUppgiftRequest = createOperativUppgiftRequest();

      var result = mapper.toCreateUppgiftRequest(createOperativUppgiftRequest);

      assertNotNull(result);
      assertEquals(createOperativUppgiftRequest.getVersion(), result.getVersion());
      assertEquals(createOperativUppgiftRequest.getHandlaggningId(), result.getHandlaggningId());
      assertEquals(createOperativUppgiftRequest.getRegel(), result.getRegel());
      assertEquals(createOperativUppgiftRequest.getBeskrivning(), result.getBeskrivning());
      assertEquals(createOperativUppgiftRequest.getVerksamhetslogik(), result.getVerksamhetslogik());
      assertEquals(createOperativUppgiftRequest.getRoll(), result.getRoll());
      assertEquals(createOperativUppgiftRequest.getUrl(), result.getUrl());
      assertEquals(createOperativUppgiftRequest.getSubTopic(), result.getSubTopic());
      assertEquals(createOperativUppgiftRequest.getErbjudande().getId(), result.getErbjudande().getId());
      assertEquals(createOperativUppgiftRequest.getErbjudande().getNamn(), result.getErbjudande().getNamn());

      assertNotNull(result.getIndivider());
      assertEquals(createOperativUppgiftRequest.getIndivider().size(), result.getIndivider().size());
      assertEquals(createOperativUppgiftRequest.getIndivider().get(0).getTypId(), result.getIndivider().get(0).getTypId());
      assertEquals(createOperativUppgiftRequest.getIndivider().get(0).getVarde(), result.getIndivider().get(0).getVarde());

      assertEquals(Map.of("source", "test-source", "type", "test-type"), result.getCloudeventAttributes());
   }

   @Test
   void toCreateUppgiftRequestMapsMultipleIndivider()
   {
      var createRequest = ImmutableCreateOperativUppgiftRequest.builder()
            .from(createOperativUppgiftRequest())
            .individer(List.of(
                  ImmutableIdtyp.builder().typId("PERSONNUMMER").varde("197001011234").build(),
                  ImmutableIdtyp.builder().typId("SAMORDNINGSNUMMER").varde("197001611234").build()))
            .build();

      var result = mapper.toCreateUppgiftRequest(createRequest);

      assertEquals(2, result.getIndivider().size());
      assertEquals("SAMORDNINGSNUMMER", result.getIndivider().get(1).getTypId());
      assertEquals("197001611234", result.getIndivider().get(1).getVarde());
   }

   @Test
   void toCreateUppgiftRequestLeavesIndividerUnsetWhenEmpty()
   {
      var createRequest = ImmutableCreateOperativUppgiftRequest.builder()
            .from(createOperativUppgiftRequest())
            .individer(List.of())
            .build();

      var result = mapper.toCreateUppgiftRequest(createRequest);

      assertNull(result.getIndivider());
   }

   @Test
   void toCreateUppgiftRequestLeavesCloudeventAttributesUnsetWhenEmpty()
   {
      var createRequest = ImmutableCreateOperativUppgiftRequest.builder()
            .from(createOperativUppgiftRequest())
            .cloudeventAttributes(Map.of())
            .build();

      var result = mapper.toCreateUppgiftRequest(createRequest);

      assertNull(result.getCloudeventAttributes());
   }

   @Test
   void toCreateUppgiftRequestCopiesCloudeventAttributes()
   {
      var createRequest = createOperativUppgiftRequest();

      var result = mapper.toCreateUppgiftRequest(createRequest);

      result.getCloudeventAttributes().put("extra", "value");
      assertEquals(2, createRequest.getCloudeventAttributes().size(),
            "Mutating the mapped request must not affect the source domain object");
   }

   @Test
   void toEndUppgiftRequestSetsReason()
   {
      var result = mapper.toEndUppgiftRequest("uppgift-avslutad");

      assertNotNull(result);
      assertEquals("uppgift-avslutad", result.getReason());
   }

   @Test
   void toOperativUppgiftMapsAllFields()
   {
      var response = new UppgiftResponse();
      response.setUppgiftId(UPPGIFT_ID);
      response.setHandlaggningId(HANDLAGGNING_ID);
      response.setStatus("TILLDELAD");
      response.setCloudeventAttributes(new java.util.HashMap<>(Map.of("source", "test-source")));

      var result = mapper.toOperativUppgift(response);

      assertNotNull(result);
      assertEquals(UPPGIFT_ID, result.getUppgiftId());
      assertEquals(HANDLAGGNING_ID, result.getHandlaggningId());
      assertEquals("TILLDELAD", result.getStatus());
      assertEquals(Map.of("source", "test-source"), result.getCloudeventAttributes());
   }

   @Test
   void toOperativUppgiftHandlesNullCloudeventAttributes()
   {
      var response = new UppgiftResponse();
      response.setUppgiftId(UPPGIFT_ID);
      response.setHandlaggningId(HANDLAGGNING_ID);
      response.setStatus("AVSLUTAD");
      response.setCloudeventAttributes(null);

      var result = mapper.toOperativUppgift(response);

      assertNotNull(result);
      assertTrue(result.getCloudeventAttributes().isEmpty());
   }

   private static CreateOperativUppgiftRequest createOperativUppgiftRequest()
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
            .erbjudande(ImmutableErbjudande.builder().id("959b609d-7402-4ef4-ad74-8c6082c9846a").namn("VAH").build())
            .build();
   }
}
