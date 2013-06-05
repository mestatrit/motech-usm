package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AllElementSearchStrategies {

    private Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

    private List<ElementSearchStrategy> searchStrategies = new ArrayList();

    public AllElementSearchStrategies() {
        searchStrategies.add(new RelativePathSearchStrategy());
        searchStrategies.add(new AbsolutePathSearchStrategy());
        searchStrategies.add(new ElementNameSearchStrategy());
    }

    public FormNode searchFirst(String searchPath, FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements ) {
        return findStrategy(searchPath).searchFirst(searchPath, startElement, rootElement, restrictedElements);
    }

    public List<FormNode> search(String searchPath, FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements ) {
        return findStrategy(searchPath).search(searchPath, startElement, rootElement, restrictedElements);
    }

    private ElementSearchStrategy findStrategy(String searchPath) {
        for(ElementSearchStrategy searchStrategy : searchStrategies) {
            if(searchStrategy.canSearch(searchPath)) {
                return searchStrategy;
            }
        }
        logger.error(String.format("Search Strategy for search path %s not found.", searchPath));
        return null;
    }





}
