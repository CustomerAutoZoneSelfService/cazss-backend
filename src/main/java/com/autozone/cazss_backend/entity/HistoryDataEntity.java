package com.autozone.cazss_backend.entity;

import com.autozone.cazss_backend.enumerator.HistoryDataTypeEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "History_data", schema = "cazss")
public class HistoryDataEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "history_data_id")
  private Integer historyDataId;

  @ManyToOne
  @JoinColumn(name = "history_id")
  private HistoryEntity history;

  @Enumerated(EnumType.STRING)
  private HistoryDataTypeEnum type;

  @Column(nullable = false)
  @Lob
  private String content;

  public HistoryDataEntity() {}

  public HistoryDataEntity(
      Integer historyDataId, HistoryEntity history, HistoryDataTypeEnum type, String content) {
    this.historyDataId = historyDataId;
    this.history = history;
    this.type = type;
    this.content = content;
  }

  public Integer getHistoryDataId() {
    return historyDataId;
  }

  public void setHistoryDataId(Integer historyDataId) {
    this.historyDataId = historyDataId;
  }

  public HistoryEntity getHistory() {
    return history;
  }

  public void setHistory(HistoryEntity history) {
    this.history = history;
  }

  public HistoryDataTypeEnum getType() {
    return type;
  }

  public void setType(HistoryDataTypeEnum type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
