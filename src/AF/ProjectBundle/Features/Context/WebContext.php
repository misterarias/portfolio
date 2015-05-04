<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 4/05/15
 * Time: 18:51
 */

namespace AF\ProjectBundle\Features\Context;


use Behat\Behat\Tester\Exception\PendingException;

class WebContext extends BaseWebContext {

    /**
     * @Then /^I should find a navigation bar with (\d+) items$/
     * @param $nItems
     */
    public function iShouldFindANavigationBarWithItems($nItems)
    {
        $this->assertSession()->elementExists("css", "#navbar");
        $this->assertSession()->elementsCount("css", "#navbar li", $nItems);
    }
}