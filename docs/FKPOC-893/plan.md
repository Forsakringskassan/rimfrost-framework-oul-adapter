# Plan: FKPOC-893 — Add support for unassign to OUL adapter

## Approach

Add `/unassign` to the regler openapi spec so the adapter can use its existing
`ReglerApi` client. No new dependency or second client needed.

## Steps

1. **Update regler openapi spec** — Add `POST /uppgifter/{uppgiftId}/unassign` to
   `rimfrost-service-oul-management-regler-openapi/openapi.yaml`, returning `UppgiftResponse`.
   Publish a new version of `rimfrost-service-oul-management-regler-api-jaxrs-spec`.

2. **Bump dependency** — Update `rimfrost-service-oul-management-regler-api-jaxrs-spec`
   version in `pom.xml` to the new version.

3. **Add adapter method** — Add `unassignOperativUppgift(UUID uppgiftId)` to `OulAdapter`,
   calling `oulClient.unassignUppgift(uppgiftId)` and mapping the response via the existing
   `toOperativUppgift(UppgiftResponse)`. Error mapping: NOT_FOUND (404),
   SERVICE_UNAVAILABLE (connection fault), UNEXPECTED_ERROR (all other HTTP errors).

4. **Tests** — Add `OulAdapterTest` cases for unassign: not found, service unavailable,
   unexpected error, and success.

## Design

### Regler spec addition
```yaml
/uppgifter/{uppgiftId}/unassign:
  post:
    summary: Lägg tillbaka uppgift i kön
    operationId: unassignUppgift
    tags: [regler]
    parameters:
      - name: uppgiftId
        in: path
        required: true
        schema:
          type: string
          format: uuid
    responses:
      '200':
        description: Uppgiften uppdaterades
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UppgiftResponse'
      '404':
        description: Uppgiften hittades inte
      '500':
        description: Internt serverfel

### OulAdapter — new method
```java
public OperativUppgift unassignOperativUppgift(UUID uppgiftId) throws OulException
{
    try
    {
        var response = oulClient.unassignUppgift(uppgiftId);
        if (response == null)
        {
            throw new OulException(OulException.ErrorType.UNEXPECTED_ERROR,
                "Oväntat fel vid återläggning av operativ uppgift, response är null för uppgiftId: " + uppgiftId);
        }
        return oulMapper.toOperativUppgift(response);
    }
    catch (NotFoundException e)
    {
        throw new OulException(OulException.ErrorType.NOT_FOUND,
            "Ingen operativ uppgift hittades med id: " + uppgiftId, e);
    }
    catch (ProcessingException e)
    {
        throw new OulException(OulException.ErrorType.SERVICE_UNAVAILABLE,
            "Kunde inte nå oul service vid återläggning av operativ uppgift med id: " + uppgiftId, e);
    }
    catch (WebApplicationException e)
    {
        throw new OulException(OulException.ErrorType.UNEXPECTED_ERROR,
            "Oväntat fel vid återläggning av operativ uppgift med id: " + uppgiftId
                + ", status: " + e.getResponse().getStatus(), e);
    }
}
```
