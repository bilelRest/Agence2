package tn.rapid_post.agence.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.rapid_post.agence.entity.Douane;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import tn.rapid_post.agence.repo.douaneRepo;

@Service

public class Reporter {
    @Autowired
    private douaneRepo douaneRepo;
public byte[] reprintdelivered(Map<String, Object> params, List<Douane> list,boolean reprin) throws JRException {
    InputStream inputStream = null;
    inputStream = getClass().getResourceAsStream("/report/recu-tous.jrxml");

    JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(list.toArray());
    JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
    JasperPrint print = JasperFillManager.fillReport(jasperReport, params, dataSource);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


    JasperExportManager.exportReportToPdfStream(print,outputStream);
    return outputStream.toByteArray() ;
}
    public byte[] reprintnotdelivered(Map<String, Object> params, List<Douane> list,boolean reprin) throws JRException {
        InputStream inputStream = null;
        inputStream = getClass().getResourceAsStream("/report/recu-sans-montant.jrxml");

        JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(list.toArray());
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JasperPrint print = JasperFillManager.fillReport(jasperReport, params, dataSource);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        JasperExportManager.exportReportToPdfStream(print,outputStream);
        return outputStream.toByteArray() ;
    }

    public byte[] printnotdelivered(Map<String, Object> params, List<Douane> list,boolean reprint) throws JRException {
        InputStream inputStream = null;
        inputStream = getClass().getResourceAsStream("/report/recu-sans-montant.jrxml");
        JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(list.toArray());
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JasperPrint print = JasperFillManager.fillReport(jasperReport, params, dataSource);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(print,outputStream);
        return outputStream.toByteArray() ;
    }
    public byte[] printdelivered(Map<String, Object> params, List<Douane> list,boolean reprint) throws JRException {
        InputStream inputStream = null;
        inputStream = getClass().getResourceAsStream("/report/recu.jrxml");
        JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(list.toArray());
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JasperPrint print = JasperFillManager.fillReport(jasperReport, params, dataSource);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(print,outputStream);
        return outputStream.toByteArray() ;
    }


}
