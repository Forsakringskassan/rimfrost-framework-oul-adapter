package se.fk.rimfrost.framework.oul.model;

import org.immutables.value.Value;
import java.util.UUID;

@Value.Immutable
public interface CreateOperativUppgiftRequest
{
   String getVersion();

   UUID getHandlaggningId();

   String getRegel();

   String getBeskrivning();

   String getVerksamhetslogik();

   String getRoll();

   String getUrl();

   String getSubTopic();

   Erbjudande getErbjudande();

   ProcessInfo getProcessInfo();
}
