package pl.ppiwd.exerciseanalyst.persistence;

import java.util.concurrent.CompletableFuture;

import pl.ppiwd.exerciseanalyst.persistence.dao.MeasurementsDao;
import pl.ppiwd.exerciseanalyst.utils.Command;

public class MeasurementSelectCommand implements Command<CompletableFuture> {

    private MeasurementsDao measurementsDao;

    public MeasurementSelectCommand(MeasurementsDao measurements) {
        this.measurementsDao = measurements;
    }

    @Override
    public CompletableFuture execute() {
        return CompletableFuture
                .supplyAsync(() -> measurementsDao.getMeasurementEntitiesForLastSession());
    }
}
