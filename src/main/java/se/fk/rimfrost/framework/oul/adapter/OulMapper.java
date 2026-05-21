package se.fk.rimfrost.framework.oul.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import se.fk.rimfrost.framework.oul.model.CreateOperativUppgiftRequest;
import se.fk.rimfrost.framework.oul.model.Erbjudande;
import se.fk.rimfrost.framework.oul.model.Idtyp;
import se.fk.rimfrost.framework.oul.model.ImmutableOperativUppgift;
import se.fk.rimfrost.framework.oul.model.OperativUppgift;
import se.fk.rimfrost.oul.management.jaxrsspec.controllers.generatedsource.model.CreateUppgiftRequest;
import se.fk.rimfrost.oul.management.jaxrsspec.controllers.generatedsource.model.EndUppgiftRequest;
import se.fk.rimfrost.oul.management.jaxrsspec.controllers.generatedsource.model.UppgiftResponse;

import java.util.HashMap;

@ApplicationScoped
public class OulMapper
{

   public CreateUppgiftRequest toCreateUppgiftRequest(CreateOperativUppgiftRequest createRequest)
   {
      var request = new CreateUppgiftRequest();
      request.setVersion(createRequest.getVersion());
      request.setHandlaggningId(createRequest.getHandlaggningId());
      request.setRegel(createRequest.getRegel());
      request.setBeskrivning(createRequest.getBeskrivning());
      request.setVerksamhetslogik(createRequest.getVerksamhetslogik());
      request.setRoll(createRequest.getRoll());
      request.setUrl(createRequest.getUrl());
      request.setSubTopic(createRequest.getSubTopic());
      request.setErbjudande(toGeneratedErbjudande(createRequest.getErbjudande()));

      if (!createRequest.getIndivider().isEmpty())
      {
         request.setIndivider(createRequest.getIndivider().stream()
               .map(this::toGeneratedIdtyp)
               .toList());
      }

      if (!createRequest.getCloudeventAttributes().isEmpty())
      {
         request.setCloudeventAttributes(new HashMap<>(createRequest.getCloudeventAttributes()));
      }

      return request;
   }

   public EndUppgiftRequest toEndUppgiftRequest(String reason)
   {
      var request = new EndUppgiftRequest();
      request.setReason(reason);
      return request;
   }

   public OperativUppgift toOperativUppgift(UppgiftResponse response)
   {
      var builder = ImmutableOperativUppgift.builder()
            .uppgiftId(response.getUppgiftId())
            .handlaggningId(response.getHandlaggningId())
            .status(response.getStatus());

      if (response.getCloudeventAttributes() != null)
      {
         builder.cloudeventAttributes(new HashMap<>(response.getCloudeventAttributes()));
      }

      return builder.build();
   }

   private se.fk.rimfrost.oul.management.jaxrsspec.controllers.generatedsource.model.Idtyp toGeneratedIdtyp(Idtyp idtyp)
   {
      var generated = new se.fk.rimfrost.oul.management.jaxrsspec.controllers.generatedsource.model.Idtyp();
      generated.setTypId(idtyp.getTypId());
      generated.setVarde(idtyp.getVarde());
      return generated;
   }

   private se.fk.rimfrost.oul.management.jaxrsspec.controllers.generatedsource.model.Erbjudande toGeneratedErbjudande(
         Erbjudande erbjudande)
   {
      var generated = new se.fk.rimfrost.oul.management.jaxrsspec.controllers.generatedsource.model.Erbjudande();
      generated.setId(erbjudande.getId());
      generated.setNamn(erbjudande.getNamn());
      return generated;
   }

}
