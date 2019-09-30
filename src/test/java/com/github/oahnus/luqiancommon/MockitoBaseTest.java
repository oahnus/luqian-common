package com.github.oahnus.luqiancommon;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * Created by oahnus on 2019/9/27
 * 9:36.
 */
public abstract class MockitoBaseTest {
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
}
