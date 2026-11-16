package com.example.ordermanagement.common.scope;

import org.springframework.stereotype.Service;

@Service
public class ScopeExperimentService {

    private final SingletonScope singletonScope;
    private final PrototypeScope prototypeScope;

    public ScopeExperimentService(SingletonScope singletonScope, PrototypeScope prototypeScope) {
        this.singletonScope = singletonScope;
        this.prototypeScope = prototypeScope;
    }

    public String getSingletonIdentifier() {
        return this.singletonScope.getIdentifier();
    }

    public String getPrototypeIdentifier() {
        return this.prototypeScope.getIdentifier();
    }
}
