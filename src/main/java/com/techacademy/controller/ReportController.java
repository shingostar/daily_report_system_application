package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model,@AuthenticationPrincipal UserDetail userDetail,Employee employee) {
        //Employee.Role.ADMIN;
        //Employee.Role.GENERAL;
        if(userDetail.getEmployee().getRole()== Employee.Role.ADMIN){
        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportList", reportService.findAll());
        }else {
            model.addAttribute("listSize", reportService.findByEmployee(userDetail.getEmployee()).size());
            model.addAttribute("reportList", reportService.findByEmployee(userDetail.getEmployee()));
        }

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable("id") Integer id, Model model) {

        model.addAttribute("report", reportService.getId(id));
        return "reports/detail";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(Model model, @ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail) {
        report.setEmployee(userDetail.getEmployee());
        model.addAttribute("report", report);
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model,
            @AuthenticationPrincipal UserDetail userDetail) {
        // ログインしている人以外には変更不可
        report.setEmployee(userDetail.getEmployee());

        // 入力チェック_"エラーがある場合は新規登録画面を表示します"
        if (res.hasErrors()) {
            return create(model, report, userDetail);
        }
        ErrorKinds result = reportService.save(report, userDetail.getEmployee());

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(model, report, userDetail);
        }
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));
            return create(model, report, userDetail);
        }

        return "redirect:/reports";
    }

    // 日報更新画面:画面から渡されてきた値に対してDBへ登録
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable("id") Integer id, Model model, Report report,
            @AuthenticationPrincipal UserDetail userDetail) {
        if (id == null) {
            model.addAttribute("report", report);

        } else
            model.addAttribute("report", reportService.getId(id));

        // 更新画面に遷移
        return "reports/update";
    }

    // 日報更新処理_"DB上に存在している日報情報”へ画面から取得した値を上書き保存して登録
    @PostMapping(value = "/{id}/update")
    public String update(@Validated Report report, BindingResult res, Model model, @PathVariable("id") Integer id,
            @AuthenticationPrincipal UserDetail userDetail) {

        report.setEmployee(userDetail.getEmployee());

        if (res.hasErrors()) {
            // エラーあり"@Validatedでの検証結果をBindingResultでresに格納、res上のエラーの有無を確認"
            return edit(null, model, report, userDetail);
        }
        // @Validatedで拾えないエラーの確認
        ErrorKinds result = reportService.update(report, id, userDetail.getEmployee());

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return edit(id, model, report, userDetail);
        }

        // Report登録
        reportService.update(report, id, userDetail.getEmployee());

        // 一覧画面にリダイレクト
        return "redirect:/reports";
    }


// 従業員削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {
        ErrorKinds result = reportService.delete(id);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            return detail(id, model);
        }

        return "redirect:/reports";
    }
}