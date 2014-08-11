#!/usr/bin/env ruby
require 'rubygems'
require 'json'
require 'curb'
require 'logger'
require 'test/unit'

class TC_HybridTest < Test::Unit::TestCase

  def setup
    super;
    @protocol = "http";
    @hostname = "localhost";
    @port = "8080";

    @log = Logger.new(STDOUT);
  end
  
  def teardown
    super;
  end
  
  def test_SitesServletAdmin
    url = "#{@protocol}://#{@hostname}:#{@port}/sakai-hybrid/sites";
    #    @log.info(url);
    curl = Curl::Easy.new(url);
    curl.headers["x-sakai-token"] = "+5JMkE44awf+2SWWZMyyzKFoJkE=;admin;-7838070940753586218";
    # execute GET
    assert(curl.http_get);
    assert_equal(200, curl.response_code);
    # body (i.e. json) should not be null
    assert_not_nil(curl.body_str);
    # puts(curl.body_str);
    assert_equal("application/json;charset=UTF-8", curl.content_type)
    json = JSON.parse(curl.body_str);
    assert_not_nil(json);
    # verify we have admin principal
    assert_not_nil(json["principal"]);
    assert_equal("admin", json["principal"]);
    # we should have some sites too
    assert_not_nil(json["sites"]);
    assert_not_nil(false, json["sites"][0]);
    assert_not_nil(false, json["sites"][1]);
    # each site should have good data
    json["sites"].each do |site|
      assert_not_nil(site["title"]);
      assert_not_nil(site["id"]);
      assert_not_nil(site["url"]);
    end
  end

  def test_SitesServletAnonymous
    url = "#{@protocol}://#{@hostname}:#{@port}/sakai-hybrid/sites";
    #    @log.info(url);
    curl = Curl::Easy.new(url);
    # execute GET
    assert(curl.http_get);
    assert_equal(200, curl.response_code);
    # body (i.e. json) should not be null
    assert_not_nil(curl.body_str);
    # puts(curl.body_str);
    assert_equal("application/json;charset=UTF-8", curl.content_type)
    json = JSON.parse(curl.body_str);
    assert_not_nil(json);
  end

  def test_SiteVisitToolPlacementServletAdmin
    url = "#{@protocol}://#{@hostname}:#{@port}/sakai-hybrid/site?siteId=!admin";
    # @log.info(url);
    curl = Curl::Easy.new(url);
    curl.headers["x-sakai-token"] = "+5JMkE44awf+2SWWZMyyzKFoJkE=;admin;-7838070940753586218";
    # execute GET
    assert(curl.http_get);
    assert_equal(200, curl.response_code);
    # puts(curl.body_str);
    # body (i.e. json) should not be null
    assert_not_nil(curl.body_str);
    assert_equal("application/json;charset=UTF-8", curl.content_type)
    json = JSON.parse(curl.body_str);
    assert_not_nil(json);
    # verify we have admin principal
    assert_not_nil(json["principal"]);
    assert_equal("admin", json["principal"]);
    # we should have a site
    assert_not_nil(json["site"]);
    assert_not_nil(json["site"]["title"]);
    assert_not_nil(json["site"]["id"]);
    assert_not_nil(json["site"]["pages"]);
    # each page should have good data
    json["site"]["pages"].each do |page|
      assert_not_nil(page["id"]);
      assert_not_nil(page["name"]);
      assert_not_nil(page["layout"]);
      assert_not_nil(page["number"]);
      assert_not_nil(page["popup"]);
      assert_not_nil(page["iconclass"]);
      assert_not_nil(page["tools"]);
      # each tool should have good data
      page["tools"].each do |tool|
        assert_not_nil(tool["url"]);
        assert_not_nil(tool["title"]);
      end
    end
    assert_not_nil(json["site"]["roles"]);
  end

  def test_SiteVisitToolPlacementServletAnonymous
    url = "#{@protocol}://#{@hostname}:#{@port}/sakai-hybrid/site?siteId=!admin";
    # @log.info(url);
    curl = Curl::Easy.new(url);
    # execute GET
    assert(curl.http_get);
    assert_equal(403, curl.response_code);
  end

end
