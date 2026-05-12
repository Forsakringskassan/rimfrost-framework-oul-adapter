package se.fk.rimfrost.framework.oul.model;

import org.immutables.value.Value;

import java.util.Map;
import java.util.UUID;

@Value.Immutable
public interface OperativUppgift
{
   UUID getUppgiftId();

   UUID getHandlaggningId();

   Map<String, String> getCloudeventAttributes();

   String getStatus();
}
