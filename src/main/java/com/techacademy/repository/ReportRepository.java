package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    //findBy"A"And"B"はJPAで自動実装されるメソッドなので下記の記述のみで使用可能
    //findBy"A"And"B"=「SELECT〜FROM〜」をAとBどちらもおこなう→AとBのデータを取得する
    Report findByEmployeeAndReportDate(Employee employee, LocalDate reportDate);
    List<Report> findByEmployee(Employee employee);
}