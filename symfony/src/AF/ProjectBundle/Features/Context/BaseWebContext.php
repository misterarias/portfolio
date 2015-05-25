<?php
namespace AF\ProjectBundle\Features\Context;

use Behat\Mink\Driver\Selenium2Driver;
use Behat\MinkExtension\Context\MinkContext;
use Behat\Symfony2Extension\Context\KernelAwareContext;
use Symfony\Component\HttpKernel\KernelInterface;

/**
 * Class BaseWebContext
 * @package Snt\AsiaBundle\MobileBundle\Features\Context
 */
class BaseWebContext extends MinkContext implements KernelAwareContext {
  /** @var  \AppKernel */
  protected $kernel;

  /**
   * Symfony method stuff for the symfony feature tests
   * @param KernelInterface $kernel
   */
  public function setKernel(KernelInterface $kernel) {
    $this->kernel = $kernel;
  }

  /**
   * @Given I have a clean browser session
   */
  public function cleanBrowserSession() {
    //XXX: I think is stated in the documentation that on every scenario the session is clean (no cookies, no nothing)
    //XXX: Until somebody checks this i'm just going to clear it just because..
    $this->clearAllCookies();
    //after killing the cookies, ask a restart
    $this->getSession()->getDriver()->stop();
    $this->getSession()->getDriver()->start();
  }

  /**
   * Removes all the cookies in the browser (phantomjs or selenium based)
   */
  public function clearAllCookies() {
    /** @var  $driver Selenium2Driver */
    //XXX: THIS IS SUPER UGLY but the f...ing phantonjs driver DOES NOT delete cookies
    $driver = $this->getSession()->getDriver();
    if ($driver instanceof Selenium2Driver) {
      $driver->getWebDriverSession()->deleteAllCookies();
    }
  }

  /**
   * Checks that an element is really a NodeElement i.e exist after a find operation
   * @param $element
   */
  protected function assertIsElement($element) {
    \PHPUnit_Framework_Assert::assertInstanceOf('Behat\Mink\Element\NodeElement', $element);
  }

  /**
   * @Given I restart the browser
   */
  public function restartBrowser() {
    $this->getSession()->getDriver()->stop();
    $this->getSession()->getDriver()->start();
  }
}
