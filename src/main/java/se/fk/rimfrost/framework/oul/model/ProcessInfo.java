package se.fk.rimfrost.framework.oul.model;

import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
public interface ProcessInfo
{
   Map<String, String> getCloudeventAttributes();

   String getReplyTopic();
}
