package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    /** findBy_A_And_B_はJPAで自動的に実装されるメソッドで下記の記述のみで使用しました */
    /** findBy_A_And_B_= SELECT C FROM D (AとBどちらも探す=AとBのデータを取得する意味です） */
    Report findByEmployeeAndReportDate(Employee employee, LocalDate reportDate);
    List<Report> findByEmployee(Employee employee);
}