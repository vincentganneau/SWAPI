package com.vincentganneau.swapi.model.dao;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.vincentganneau.swapi.model.database.SWDatabase;
import com.vincentganneau.swapi.model.entity.Planet;
import com.vincentganneau.swapi.testing.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Class that tests the {@link PlanetDao} interface.
 * @author Vincent Ganneau
 */
@RunWith(AndroidJUnit4.class)
public class PlanetDaoTest {

    /**
     * The rule that forces Architecture Components to instantly execute any background operation on the calling thread.
     */
    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    // Database
    private SWDatabase mDatabase;

    // Observer
    @Mock private Observer<List<Planet>> mObserver;

    /**
     * Initializes mock objects and builds the database by allowing main thread queries for the purpose of testing.
     */
    @Before
    public void buildDatabase() {
        MockitoAnnotations.initMocks(this);
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), SWDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    /**
     * Tests inserting and loading planets.
     */
    @Test
    public void testInsertAndLoadPlanets() {
        // Given
        final List<Planet> planets = Arrays.asList(TestUtils.PLANETS[0], TestUtils.PLANETS[1]);
        mDatabase.planetDao().loadPlanets().observeForever(mObserver);

        // When
        mDatabase.planetDao().insertPlanet(TestUtils.PLANETS[0]);
        mDatabase.planetDao().insertPlanet(TestUtils.PLANETS[1]);
        mDatabase.planetDao().insertPlanets(planets);

        // Then
        verify(mObserver, times(1)).onChanged(Collections.emptyList());
        verify(mObserver, times(1)).onChanged(Collections.singletonList(TestUtils.PLANETS[0]));
        verify(mObserver, times(2)).onChanged(planets);
    }

    /**
     * Closes the database.
     */
    @After
    public void closeDatabase() {
        mDatabase.close();
    }
}
