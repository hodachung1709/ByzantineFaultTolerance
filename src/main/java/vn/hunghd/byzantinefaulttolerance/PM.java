/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

import config.ConfigSystem;
import helper.MyRound;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class PM {

    public int time;
    public int idPM;
    private double temp;
    private double cpu, ram, disk;//peformance %
    private double ccpu, cram, cdisk;//capacity
    private double ucpu, uram, updisk;//utility
    private double acpu, aram, adisk;// available;
    private double bandwidth;
    private ArrayList<VM> vms = new ArrayList<>();

    public PM() {
    }

    public PM(int time, int idPM, double cpu, double ram, double disk, double temp, double ccpu, double cram, double cdisk, double bandwidth) {
        this.time = time;
        this.idPM = idPM;

        this.cpu = cpu;
        this.ram = ram;
        this.disk = disk;
        this.temp = temp;

        this.ccpu = ccpu;
        this.cram = cram;
        this.cdisk = cdisk;

        this.bandwidth = bandwidth;
    }

    public PM(PM pm) {
        this.time = pm.time;
        this.idPM = pm.idPM;
        this.cpu = pm.cpu;
        this.ram = pm.ram;
        this.disk = pm.disk;
        this.temp = pm.temp;
        this.ccpu = pm.ccpu;
        this.cram = pm.cram;
        this.cdisk = pm.cdisk;
        this.bandwidth = pm.bandwidth;
    }

    public double getUtilizationLoad() {
        return MyRound.getRound(ConfigSystem.gamma[0] * cpu + ConfigSystem.gamma[1] * ram + ConfigSystem.gamma[2] * disk);
    }

    public double getAvailableLoad() {
        return MyRound.getRound(ConfigSystem.gamma[0] * (1 - cpu) + ConfigSystem.gamma[1] * (1 - ram) + ConfigSystem.gamma[2] * (1 - disk));
    }

    public double getCcpu() {
        return ccpu;
    }

    public double getCram() {
        return cram;
    }

    public double getCdisk() {
        return cdisk;
    }

    public double getUcpu() {
        return cpu * ccpu;
    }

    public double getUram() {
        return ram * cram;
    }

    public double getUpdisk() {
        return disk * cdisk;
    }

    public double getAcpu() {
        return ccpu - cpu * ccpu;
    }

    public double getAram() {
        return cram - ram * cram;
    }

    public double getAdisk() {
        return cdisk - disk * cdisk;
    }

    public double getCpu() {
        return cpu;
    }

    public double getRam() {
        return ram;
    }

    public double getDisk() {
        return disk;
    }

    public void setCpu(double cpu) {
        double c = cpu / ccpu;
        this.cpu += c;
    }

    public void setRam(double ram) {
        double r = ram / cram;
        this.ram += r;
    }
    
    public void setDisk(double disk) {
        double d = disk / cdisk;
        this.disk += d;
    }
    
    public void setECpu(double cpu) {
        
        this.cpu = cpu;
    }

    public void setERam(double ram) {
       
        this.ram = ram;
    }

    public void setEDisk(double disk) {
       
        this.disk = disk;
    }

    public void addVM2PM(VM vm) {
        this.vms.add(vm);
        //update cpu,ram,disk
        this.cpu += vm.getCcpu() / ccpu;
        this.ram += vm.getCram() / cram;
        this.disk += vm.getCdisk() / cdisk;
    }

    public boolean addableVM(VM vm) {
        if (cpu * ccpu - vm.getCcpu() > 0
                && ram * cram - vm.getCram() > 0
                && disk * cdisk - vm.getCdisk() > 0) {
            return true;
        }
        return false;
    }

    public void removeRecentVM(VM vm) {
        this.vms.remove(vms.size() - 1);
        this.cpu -= vm.getCcpu() / ccpu;
        this.ram -= vm.getCram() / cram;
        this.disk -= vm.getCdisk() / cdisk;
    }

    public ArrayList<VM> getVms() {
        return vms;
    }

    public double getTemp() {
        return temp;
    }

    public double getBandwidth() {
        return bandwidth;
    }

    public void setCcpu(double ccpu) {
        this.ccpu = ccpu;
    }

    public void setCram(double cram) {
        this.cram = cram;
    }

    public void setCdisk(double cdisk) {
        this.cdisk = cdisk;
    }

    public static double CalculatePMsLoadBalancing(List<PM> pms) {
        double loadBalancing = 0;
        double utilMean = 0;

        for (PM pm : pms) {
            utilMean += pm.getUtilizationLoad();
        }

        utilMean /= pms.size();

        for (PM pm : pms) {
            loadBalancing += Math.pow(pm.getUtilizationLoad() - utilMean, 2);
        }

        loadBalancing /= (pms.size() - 1);
        loadBalancing = Math.sqrt(loadBalancing);

        return loadBalancing;
    }

}
