package com.duallab.bustimetable;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.TreeSet;

public class TimeTable {

    public static final String POSH = "Posh";
    public static final String GROTTY = "Grotty";


    private TreeSet<BusService> poshBusServiceList = new TreeSet<>();
    private TreeSet<BusService> grottyBusServiceList = new TreeSet<>();

    public TimeTable() {

    }

    public void generateTimeTable(FileReader fileReader, String outputLocation) {
        this.getOriginalTimeTable(fileReader);
        this.checkTimeTableOfServices();
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        try {
            LocalTime currTime = LocalTime.now();
            String location = outputLocation + "timetable_" + currTime.getHour() + "_" + currTime.getMinute() + "_" + ".txt";
            Path path = Paths.get(location);
            if (path != null) {
                fileWriter = new FileWriter(path.toFile());
                bufferedWriter = new BufferedWriter(fileWriter);
                boolean isPoshServiceLst = false;
                Iterator iterator;
                if (this.poshBusServiceList != null && this.poshBusServiceList.size() > 0) {
                    isPoshServiceLst = true;
                    iterator = this.poshBusServiceList.iterator();

                    while(iterator.hasNext()) {
                        bufferedWriter.write(((BusService)iterator.next()).toString());
                        bufferedWriter.newLine();
                    }
                }

                if (this.grottyBusServiceList != null && this.grottyBusServiceList.size() > 0) {
                    if (isPoshServiceLst) {
                        bufferedWriter.newLine();
                    }

                    iterator = this.grottyBusServiceList.iterator();

                    while(iterator.hasNext()) {
                        bufferedWriter.write(((BusService)iterator.next()).toString());
                        bufferedWriter.newLine();
                    }
                }

                System.out.println("Output timetable generated at location:" + path.toString());
                return;
            }

            System.out.println("Output path is not properly defined. Now Exiting, please try again!");
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }

                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void checkTimeTableOfServices() {
        if (this.poshBusServiceList != null && this.poshBusServiceList.size() > 0 && this.grottyBusServiceList != null && this.grottyBusServiceList.size() > 0) {
            Object[] poshBusService = this.poshBusServiceList.toArray();
            Object[] grottyBusService = this.grottyBusServiceList.toArray();

            for(int i = 0; i < poshBusService.length; i++) {
                BusService poshService = (BusService)poshBusService[i];
                if (poshService != null) {
                    for(int j = 0; j < grottyBusService.length; j++) {
                        BusService grottyService = (BusService)grottyBusService[j];
                        if (grottyService != null) {
                            int result = this.compareAndRemoveInefficentService(poshService, grottyService);
                            if (result == 1) {
                                poshBusService[i] = null;
                                break;
                            }

                            if (result == 2) {
                                grottyBusService[j] = null;
                            }
                        }
                    }
                }
            }
        }

    }

    private int compareAndRemoveInefficentService(BusService poshService, BusService grottyService) {
        LocalTime poshBusDepTime = poshService.getDepartureTime();
        LocalTime poshBusArrTime = poshService.getArrivalTime();
        LocalTime grottyBusDepTime = grottyService.getDepartureTime();
        LocalTime grottyBusArrTime = grottyService.getArrivalTime();
        if (poshBusDepTime.compareTo(grottyBusDepTime) == 0 && poshBusArrTime.compareTo(grottyBusArrTime) == 0) {
            this.grottyBusServiceList.remove(grottyService);
            return 2;
        } else {
            int result;
            if (poshBusDepTime.compareTo(grottyBusDepTime) == 0) {
                result = poshBusArrTime.compareTo(grottyBusArrTime);
                if (result == 1) {
                    this.poshBusServiceList.remove(poshService);
                    return 1;
                } else {
                    this.grottyBusServiceList.remove(grottyService);
                    return 2;
                }
            } else if (poshBusArrTime.compareTo(grottyBusArrTime) == 0) {
                result = poshBusDepTime.compareTo(grottyBusDepTime);
                if (result == 1) {
                    this.grottyBusServiceList.remove(grottyService);
                    return 2;
                } else {
                    this.poshBusServiceList.remove(poshService);
                    return 1;
                }
            } else if ((!poshBusDepTime.isAfter(grottyBusDepTime) || !poshBusDepTime.isBefore(grottyBusArrTime) && poshBusDepTime.compareTo(grottyBusArrTime) != 0) && (!grottyBusDepTime.isAfter(poshBusDepTime) || !grottyBusDepTime.isBefore(poshBusArrTime) && grottyBusDepTime.compareTo(poshBusArrTime) != 0)) {
                return 0;
            } else {
                int poshBusTotalJourneyTime = poshBusArrTime.toSecondOfDay() - poshBusDepTime.toSecondOfDay();
                int grottyBusTotalJourneyTime = grottyBusArrTime.toSecondOfDay() - grottyBusDepTime.toSecondOfDay();
                result = Integer.compare(poshBusTotalJourneyTime, grottyBusTotalJourneyTime);
                if (result != 0 && result != -1) {
                    this.poshBusServiceList.remove(poshService);
                    return 1;
                } else {
                    this.grottyBusServiceList.remove(grottyService);
                    return 2;
                }
            }
        }
    }

    private void getOriginalTimeTable(FileReader fileReader) {
        BufferedReader bufferedReader = null;
        String line = null;

        try {
            bufferedReader = new BufferedReader(fileReader);

            String[] service;
            boolean isPosh;
            for(int noOfEnteries = 0; (line = bufferedReader.readLine()) != null; this.setServiceDTO(service, isPosh)) {
                ++noOfEnteries;
                if (noOfEnteries > 50) {
                    break;
                }

                service = line.split(" ");
                String companyName = service[0];
                isPosh = true;
                if (companyName.equals("Grotty")) {
                    isPosh = false;
                }
            }
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void setServiceDTO(String[] service, boolean isPosh) {
        String companyName = service[0];
        String[] depTime = service[1].split(":");
        LocalTime departureTime = LocalTime.of(Integer.parseInt(depTime[0]), Integer.parseInt(depTime[1]));
        String[] arrTime = service[2].split(":");
        LocalTime arrivalTime = LocalTime.of(Integer.parseInt(arrTime[0]), Integer.parseInt(arrTime[1]));
        long netDuration = (long)(arrivalTime.toSecondOfDay() - departureTime.toSecondOfDay());
        if (netDuration <= 3600L) {
            TreeSet busServiceList;
            if (isPosh) {
                busServiceList = this.poshBusServiceList == null ? (this.poshBusServiceList = new TreeSet()) : this.poshBusServiceList;
            } else {
                busServiceList = this.grottyBusServiceList == null ? (this.grottyBusServiceList = new TreeSet()) : this.grottyBusServiceList;
            }

            BusService busService = new BusService();
            busService.setCompanyName(companyName);
            busService.setDepartureTime(departureTime);
            busService.setArrivalTime(arrivalTime);
            busServiceList.add(busService);
        }
    }

}


