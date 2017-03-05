package com.finalysis.research;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;

/**
 * Created by yangf on 2017/3/5.
 */
public class SimpleImplicitNamingStrategy extends SpringImplicitNamingStrategy {

    public SimpleImplicitNamingStrategy() {
    }

    @Override
    public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
        return toIdentifier( transformAttributePath( source.getAttributePath() ), source.getBuildingContext() );
    }
}
