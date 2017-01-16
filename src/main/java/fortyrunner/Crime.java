package fortyrunner;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.Objects;

/**
 * With 5 fields
 * 13:03:49.068 [Camel (camel-2) thread #0 - file://src/data] INFO  - Added to Ignite, cache=6243319, took=139188 ms
 * <p>
 * With 6 fields
 */
public class Crime {

  private String id;

  private String caseNumber;

  @QuerySqlField(index = true)
  private String primaryType;

  private String date;

  private String description;

  private String descriptionLocation;

  private String arrest;

  public void setId(final String id) {
    this.id = id;
  }

  public void setCaseNumber(final String caseNumber) {
    this.caseNumber = caseNumber;
  }

  public void setPrimaryType(final String primaryType) {
    this.primaryType = primaryType;
  }

  public void setDate(final String date) {
    this.date = date;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setDescriptionLocation(final String descriptionLocation) {
    this.descriptionLocation = descriptionLocation;
  }

  public void setArrest(final String arrest) {
    this.arrest = arrest;
  }

  public String getPrimaryType() {
    return primaryType;
  }

  public String getId() {
    return id;
  }

  public String getCaseNumber() {
    return caseNumber;
  }

  public String getDate() {
    return date;
  }

  public String getDescription() {
    return description;
  }

  public String getDescriptionLocation() {
    return descriptionLocation;
  }

  public String getArrest() {
    return arrest;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Crime crime = (Crime) o;
    return Objects.equals(id, crime.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
