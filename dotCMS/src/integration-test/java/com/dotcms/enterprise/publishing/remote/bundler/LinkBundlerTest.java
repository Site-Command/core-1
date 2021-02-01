package com.dotcms.enterprise.publishing.remote.bundler;

import com.dotcms.datagen.*;
import com.dotcms.publisher.pusher.PushPublisherConfig;
import com.dotcms.publishing.BundlerStatus;
import com.dotcms.publishing.DotBundleException;
import com.dotcms.publishing.FilterDescriptor;
import com.dotcms.publishing.PublisherConfig;
import com.dotcms.test.util.FileTestUtil;
import com.dotcms.util.IntegrationTestInitService;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.util.FileUtil;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.dotcms.util.CollectionsUtils.list;
import static com.dotcms.util.CollectionsUtils.set;
import static org.mockito.Mockito.mock;

@RunWith(DataProviderRunner.class)
public class LinkBundlerTest {


    public static void prepare() throws Exception {
        //Setting web app environment
        IntegrationTestInitService.getInstance().init();
    }

    @DataProvider
    public static Object[] links() throws Exception {
        prepare();

        final Host host = new SiteDataGen().nextPersisted();
        final Folder folder = new FolderDataGen().site(host).nextPersisted();

        final Link link = new LinkDataGen(folder)
                .hostId(host.getIdentifier())
                .nextPersisted();

        Link liveLink = new LinkDataGen(folder)
                .hostId(host.getIdentifier())
                .nextPersisted(true);

        return new LinkBundlerTest.TestCase[]{
                new LinkBundlerTest.TestCase(link),
                new LinkBundlerTest.TestCase(liveLink)
        };
    }

    @Test
    @UseDataProvider("links")
    public void addLinkInBundle(final LinkBundlerTest.TestCase testCase)
            throws DotBundleException, IOException, DotSecurityException, DotDataException {

        final List<Link> links = testCase.links;

        final BundlerStatus status = mock(BundlerStatus.class);
        final LinkBundler bundler = new LinkBundler();
        final File bundleRoot = FileUtil.createTemporaryDirectory("LinkBundlerTest_addLinkInBundle");

        final FilterDescriptor filterDescriptor = new FileDescriptorDataGen().nextPersisted();

        final PushPublisherConfig config = new PushPublisherConfig();
        config.setLinks(set( links.get(0).getIdentifier()));
        config.setOperation(PublisherConfig.Operation.PUBLISH);

        new BundleDataGen()
                .pushPublisherConfig(config)
                .addAssets(list(links.get(0)))
                .filter(filterDescriptor)
                .nextPersisted();

        bundler.setConfig(config);
        bundler.generate(bundleRoot, status);

        for (final Link link : links) {
            FileTestUtil.assertBundleFile(bundleRoot, link, testCase.expectedFilePath);
        }
    }

    private static class TestCase{
        List<Link> links;
        String expectedFilePath;

        public TestCase(final Link link) {
            this(list(link));
        }

        public TestCase(final List<Link> links, final String expectedFilePath) {
            this.links = links;
            this.expectedFilePath = expectedFilePath;
        }

        public TestCase(final List<Link> links) {
            this(links, "/bundlers-test/hos");
        }
    }
}
