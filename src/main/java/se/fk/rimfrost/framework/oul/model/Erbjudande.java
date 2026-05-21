package se.fk.rimfrost.framework.oul.model;

import org.immutables.value.Value;

@Value.Immutable
public interface Erbjudande
{
   String getId();

   String getNamn();
}
