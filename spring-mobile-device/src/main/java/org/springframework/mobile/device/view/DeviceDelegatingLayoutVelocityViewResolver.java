package org.springframework.mobile.device.view;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.mobile.device.site.SitePreferenceUtils;
import org.springframework.mobile.device.util.ResolverUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.velocity.VelocityLayoutView;
import org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver;

/**
 *
 * A {@link AbstractDeviceDelegatingViewResolver} for adjusting a
 * layout aware Velocity view based on the combination of resolved {@link Device} and specified
 * {@link SitePreference}. View names can be augmented with a specified prefix
 * or suffix, and a layout.
 *
 * <p>The prefix and suffix works in the same way as {@link org.springframework.mobile.device.view.LiteDeviceDelegatingViewResolver}.</p>
 *
 * <p>Then the specified layout is applied, if specified. If not, the layout given to the delegating
 * VelocityLayoutViewResolver is applied</p>
 * <p>If fallback is enable, the layout of the VelocityLayoutViewResolver is applied.</p>
 *
 * @see org.springframework.mobile.device.view.LiteDeviceDelegatingViewResolver
 * @author Augustin Trancart
 * @version 0.1
 */
public class DeviceDelegatingLayoutVelocityViewResolver extends AbstractDeviceDelegatingViewResolver {

    private String normalPrefix = "";

    private String mobilePrefix = "";

    private String tabletPrefix = "";

    private String normalSuffix = "";

    private String mobileSuffix = "";

    private String tabletSuffix = "";

    private String normalLayoutUrl = "";

    private String mobileLayoutUrl = "";

    private String tableLayoutUrl = "";

	/**
	 * Creates a new LiteDeviceDelegatingViewResolver
	 * @param delegate the ViewResolver in which to delegate
	 */
	public DeviceDelegatingLayoutVelocityViewResolver(VelocityLayoutViewResolver delegate) {
		super(delegate);
	}

	/**
	 * Set the prefix that gets prepended to view names for normal devices.
	 */
	public void setNormalPrefix(String normalPrefix) {
		this.normalPrefix = (normalPrefix != null ? normalPrefix : "");
	}

	/**
	 * Return the prefix that gets prepended to view names for normal devices
	 */
	protected String getNormalPrefix() {
		return this.normalPrefix;
	}

	/**
	 * Set the prefix that gets prepended to view names for mobile devices.
	 */
	public void setMobilePrefix(String mobilePrefix) {
		this.mobilePrefix = (mobilePrefix != null ? mobilePrefix : "");
	}

	/**
	 * Return the prefix that gets prepended to view names for mobile devices
	 */
	protected String getMobilePrefix() {
		return this.mobilePrefix;
	}

	/**
	 * Set the prefix that gets prepended to view names for tablet devices.
	 */
	public void setTabletPrefix(String tabletPrefix) {
		this.tabletPrefix = (tabletPrefix != null ? tabletPrefix : "");
	}

	/**
	 * Return the prefix that gets prepended to view names for tablet devices
	 */
	protected String getTabletPrefix() {
		return this.tabletPrefix;
	}

	/**
	 * Set the suffix that gets appended to view names for normal devices.
	 */
	public void setNormalSuffix(String normalSuffix) {
		this.normalSuffix = (normalSuffix != null ? normalSuffix : "");
	}

	/**
	 * Return the suffix that gets appended to view names for normal devices
	 */
	protected String getNormalSuffix() {
		return this.normalSuffix;
	}

	/**
	 * Set the suffix that gets appended to view names for mobile devices
	 */
	public void setMobileSuffix(String mobileSuffix) {
		this.mobileSuffix = (mobileSuffix != null ? mobileSuffix : "");
	}

	/**
	 * Return the suffix that gets appended to view names for mobile devices
	 */
	protected String getMobileSuffix() {
		return this.mobileSuffix;
	}

	/**
	 * Set the suffix that gets appended to view names for tablet devices
	 */
	public void setTabletSuffix(String tabletSuffix) {
		this.tabletSuffix = (tabletSuffix != null ? tabletSuffix : "");
	}

	/**
	 * Return the suffix that gets appended to view names for tablet devices
	 */
	protected String getTabletSuffix() {
		return this.tabletSuffix;
	}

    public String getNormalLayoutUrl() {
        return normalLayoutUrl;
    }

    public void setNormalLayoutUrl(String normalLayoutUrl) {
        this.normalLayoutUrl = normalLayoutUrl;
    }

    public String getMobileLayoutUrl() {
        return mobileLayoutUrl;
    }

    public void setMobileLayoutUrl(String mobileLayoutUrl) {
        this.mobileLayoutUrl = mobileLayoutUrl;
    }

    public String getTableLayoutUrl() {
        return tableLayoutUrl;
    }

    public void setTableLayoutUrl(String tableLayoutUrl) {
        this.tableLayoutUrl = tableLayoutUrl;
    }

    @Override
	protected String getDeviceViewNameInternal(String viewName) {
		RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
		Assert.isInstanceOf(ServletRequestAttributes.class, attrs);
		HttpServletRequest request = ((ServletRequestAttributes) attrs).getRequest();
		Device device = DeviceUtils.getCurrentDevice(request);
		SitePreference sitePreference = SitePreferenceUtils.getCurrentSitePreference(request);
		String resolvedViewName = viewName;
		if (ResolverUtils.isNormal(device, sitePreference)) {
			resolvedViewName = getNormalPrefix() + viewName + getNormalSuffix();
		} else if (ResolverUtils.isMobile(device, sitePreference)) {
			resolvedViewName = getMobilePrefix() + viewName + getMobileSuffix();
		} else if (ResolverUtils.isTablet(device, sitePreference)) {
			resolvedViewName = getTabletPrefix() + viewName + getTabletSuffix();
		}
		return resolvedViewName;
	}

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
		String deviceViewName = getDeviceViewName(viewName);
		VelocityLayoutView view = (VelocityLayoutView) getViewResolver().resolveViewName(deviceViewName, locale);
		if (getEnableFallback() && view == null) {
			view = (VelocityLayoutView) getViewResolver().resolveViewName(viewName, locale);
		}
        else {
            //that's where we put the good layout.
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            Assert.isInstanceOf(ServletRequestAttributes.class, attrs);
            HttpServletRequest request = ((ServletRequestAttributes) attrs).getRequest();
            Device device = DeviceUtils.getCurrentDevice(request);
            SitePreference sitePreference = SitePreferenceUtils.getCurrentSitePreference(request);
            if (ResolverUtils.isNormal(device, sitePreference) && StringUtils.hasText(normalLayoutUrl)) {
                view.setLayoutUrl(normalLayoutUrl);
            } else if (ResolverUtils.isMobile(device, sitePreference) && StringUtils.hasText(mobileLayoutUrl)) {
                view.setLayoutUrl(mobileLayoutUrl);
            } else if (ResolverUtils.isTablet(device, sitePreference) && StringUtils.hasText(tableLayoutUrl)) {
                view.setLayoutUrl(tableLayoutUrl);
            }
        }
		return view;
	}

}
