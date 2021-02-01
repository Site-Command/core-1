package com.dotcms.test.util.assertion;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.rules.model.Rule;
import com.liferay.portal.model.User;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import static com.dotcms.util.CollectionsUtils.map;

public class RuleAssertionChecker implements AssertionChecker<Rule> {
    @Override
    public Map<String, Object> getFileArguments(Rule rule, File file) {
        return map(
            "id", rule.getId(),
            "name", rule.getName(),
            "parent", rule.getParent()
        );
    }

    @Override
    public String getFilePathExpected(File file) {
        return "/bundlers-test/rule/rule.rule.xml";
    }

    @Override
    public File getFileInner(final Rule rule, final File bundleRoot) {
        final User systemUser = APILocator.systemUser();
        try {
            final Host host = APILocator.getHostAPI().find(rule.getParent(), systemUser, false);

            final String urlFilePath = bundleRoot.getPath() + File.separator + "live" + File.separator + host.getHostname()
                    + File.separator + rule.getId() + ".rule.xml";
            return new File(urlFilePath);

        } catch (DotDataException | DotSecurityException e) {
            throw new RuntimeException();
        }

    }

}
