package se.fk.rimfrost.framework.oul.adapter;

import jakarta.annotation.PreDestroy;
import se.fk.rimfrost.framework.oul.exception.OulException;
import se.fk.rimfrost.framework.oul.model.CreateOperativUppgiftRequest;
import se.fk.rimfrost.framework.oul.model.OperativUppgift;
import se.fk.rimfrost.oul.management.regler.jaxrsspec.controllers.generatedsource.ReglerApi;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.apache5.connector.Apache5ConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import java.util.UUID;

@SuppressWarnings("unused")
@ApplicationScoped
public class OulAdapter
{
   @ConfigProperty(name = "oul.api.base-url")
   String oulBaseUrl;

   private ReglerApi oulClient;

   Client client;

   @Inject
   OulMapper oulMapper;

   @PostConstruct
   void init()
   {
      ClientConfig clientConfig = new ClientConfig();
      clientConfig.connectorProvider(new Apache5ConnectorProvider());
      this.client = ClientBuilder.newClient(clientConfig);
      this.oulClient = WebResourceFactory.newResource(
            ReglerApi.class,
            client.target(this.oulBaseUrl));
   }

   @PreDestroy
   void destroy()
   {
      this.oulClient = null;

      if (this.client != null)
      {
         this.client.close();
         this.client = null;
      }
   }

   public OperativUppgift createOperativUppgift(CreateOperativUppgiftRequest createRequest) throws OulException
   {
      try
      {
         var createUppgiftRequest = oulMapper.toCreateUppgiftRequest(createRequest);
         var createUppgiftResponse = oulClient.createUppgift(createUppgiftRequest);
         if (createUppgiftResponse == null)
         {
            throw new OulException(OulException.ErrorType.UNEXPECTED_ERROR,
                  "Oväntat fel vid skapande av operativ uppgift, response är null");
         }

         return oulMapper.toOperativUppgift(createUppgiftResponse);
      }
      catch (BadRequestException e)
      {
         throw new OulException(OulException.ErrorType.BAD_REQUEST,
               "Felaktig förfrågan vid skapande av operativ uppgift", e);
      }
      catch (ProcessingException e)
      {
         throw new OulException(OulException.ErrorType.SERVICE_UNAVAILABLE,
               "Kunde inte nå oul service", e);
      }
      catch (WebApplicationException e)
      {
         throw new OulException(OulException.ErrorType.UNEXPECTED_ERROR,
               "Oväntat fel vid skapande av operativ uppgift, status: " + e.getResponse().getStatus(), e);
      }
   }

   public OperativUppgift endOperativUppgift(UUID uppgiftId, String reason) throws OulException
   {
      try
      {
         var endUppgiftRequest = oulMapper.toEndUppgiftRequest(reason);
         var endUppgiftResponse = oulClient.endUppgift(uppgiftId, endUppgiftRequest);
         if (endUppgiftResponse == null)
         {
            throw new OulException(OulException.ErrorType.UNEXPECTED_ERROR,
                  "Oväntat fel vid avslut av operativ uppgift, response är null för uppgiftId: " + uppgiftId);
         }
         return oulMapper.toOperativUppgift(endUppgiftResponse);
      }
      catch (NotFoundException e)
      {
         throw new OulException(OulException.ErrorType.NOT_FOUND,
               "Ingen operativ uppgift hittades med id: " + uppgiftId, e);
      }
      catch (BadRequestException e)
      {
         throw new OulException(OulException.ErrorType.BAD_REQUEST,
               "Felaktig förfrågan vid avslut av operativ uppgift med id: " + uppgiftId, e);
      }
      catch (ProcessingException e)
      {
         throw new OulException(OulException.ErrorType.SERVICE_UNAVAILABLE,
               "Kunde inte nå oul service vid avslut av operativ uppgift med id: " + uppgiftId, e);
      }
      catch (WebApplicationException e)
      {
         throw new OulException(OulException.ErrorType.UNEXPECTED_ERROR,
               "Oväntat fel vid avslut av operativ uppgift med id: " + uppgiftId + ", status: "
                     + e.getResponse().getStatus(),
               e);
      }
   }

}
