package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    ;

    @Autowired
    public ReportService(ReportRepository reportRepository, PasswordEncoder passwordEncoder) {
        this.reportRepository = reportRepository;

    }

    // 日報保存（新規登録）
    @Transactional
    /** コントローラ側で受け取ったログインしているユーザー情報（Employee employee）と日付（Report report）を引数に格納します。*/
    public ErrorKinds save(Report report, Employee employee) {

        /** 重複する条件 */
        /** 日報テーブルに"ログイン中の従業員" かつ "入力した日付"の日報データが存在する場合エラー表示されます。 */
        // nullでない場合ログインユーザーと入力した日付が見つかったらnullではない値が返ってくる。そのときエラー処理を行います。

        if (findByEmployeeAndReportDate(report, employee) != null) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Report report, Integer id,Employee employee) {

        /** 日報テーブルに"画面で表示中の従業員" かつ "入力した日付"の日報データが存在する場合エラー表示が出ます。 */
        /** 画面で表示中の日報データを除いたものについて、上記のチェックを行なうものとします。 */
        /** 更新の場合、日付が変わったかつすでに社員のコード＋日付がある場合はエラー表示が出ます。 */

        Report reportList = findByEmployeeAndReportDate(report, employee);

        if (reportList != null && id != reportList.getId()) {

            return ErrorKinds.DATECHECK_ERROR;
        }

        Report dbReport = findById(id);

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setCreatedAt(dbReport.getCreatedAt());

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 従業員削除
      @Transactional public ErrorKinds delete(@PathVariable ("id") Integer id) {

      Report report = findById(id);
      LocalDateTime now = LocalDateTime.now();
      report.setUpdatedAt(now);
      report.setDeleteFlg(true);

      return ErrorKinds.SUCCESS; }


    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }

    // 1件を検索
    public Report findById(Integer id) {
        /** findByIdで検索 */
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // idを1件検索して返します
    public Report getId(Integer id) {
        return reportRepository.findById(id).get();
    }

    // ログインユーザーの情報と入力した日付を検索
    public Report findByEmployeeAndReportDate(Report report, Employee employee) {
        /** DBの情報を取得するにはリポジトリを介して行う→findByEmployeeAndReportDateをリポジトリで定義する必要があります。 */
        return reportRepository.findByEmployeeAndReportDate(employee, report.getReportDate());
    }
}