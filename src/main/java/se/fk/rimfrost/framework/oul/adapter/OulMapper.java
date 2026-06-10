package se.fk.rimfrost.framework.oul.adapter;

import jakarta.enterprise.context.ApplicationScoped;
import se.fk.rimfrost.framework.oul.model.CreateOperativUppgiftRequest;
import se.fk.rimfrost.framework.oul.model.Erbjudande;
import se.fk.rimfrost.framework.oul.model.Idtyp;
import se.fk.rimfrost.framework.oul.model.ImmutableOperativUppgift;
import se.fk.rimfrost.framework.oul.model.ImmutableProcessInfo;
import se.fk.rimfrost.framework.oul.model.OperativUppgift;
import se.fk.rimfrost.oul.management.regler.jaxrsspec.controllers.generatedsource.model.CreateUppgiftRequest;
import se.fk.rimfrost.oul.management.regler.jaxrsspec.controllers.generatedsource.model.EndUppgiftRequest;
import se.fk.rimfrost.oul.management.regler.jaxrsspec.controllers.generatedsource.model.ProcessInfo;
import se.fk.rimfrost.oul.management.regler.jaxrsspec.controllers.generatedsource.model.UppgiftResponse;

import java.util.HashMap;

@ApplicationScoped
public class OulMapper
{

   public CreateUppgiftRequest toCreateUppgiftRequest(CreateOperativUppgiftRequest createRequest)
   {
      var processInfo = new ProcessInfo();
      processInfo.setReplyTopic(createRequest.getProcessInfo().getReplyTopic());

      if (!createRequest.getProcessInfo().getCloudeventAttributes().isEmpty())
      {
         processInfo.setCloudeventAttributes(new HashMap<>(createRequest.getProcessInfo().getCloudeventAttributes()));
      }

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
      request.setProcessInfo(processInfo);

      if (!createRequest.getIndivider().isEmpty())
      {
         request.setIndivider(createRequest.getIndivider().stream()
               .map(this::toGeneratedIdtyp)
               .toList());
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
      var processInfoBuilder = ImmutableProcessInfo.builder()
            .replyTopic(response.getProcessInfo().getReplyTopic());

      if (response.getProcessInfo().getCloudeventAttributes() != null)
      {
         processInfoBuilder.cloudeventAttributes(new HashMap<>(response.getProcessInfo().getCloudeventAttributes()));
      }

      return ImmutableOperativUppgift.builder()
            .uppgiftId(response.getUppgiftId())
            .handlaggningId(response.getHandlaggningId())
            .status(response.getStatus())
            .processInfo(processInfoBuilder.build()).build();
   }

   private se.fk.rimfrost.oul.management.regler.jaxrsspec.controllers.generatedsource.model.Idtyp toGeneratedIdtyp(Idtyp idtyp)
   {
      var generated = new se.fk.rimfrost.oul.management.regler.jaxrsspec.controllers.generatedsource.model.Idtyp();
      generated.setTypId(idtyp.getTypId());
      generated.setVarde(idtyp.getVarde());
      return generated;
   }

   private se.fk.rimfrost.oul.management.regler.jaxrsspec.controllers.generatedsource.model.Erbjudande toGeneratedErbjudande(
         Erbjudande erbjudande)
   {
      var generated = new se.fk.rimfrost.oul.management.regler.jaxrsspec.controllers.generatedsource.model.Erbjudande();
      generated.setId(erbjudande.getId());
      generated.setNamn(erbjudande.getNamn());
      return generated;
   }

}
