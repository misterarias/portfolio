<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 07/05/15
 * Time: 14:31
 */

namespace AF\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\OptionsResolver\Exception\AccessException;

/**
 * Class AFController
 *
 * This class is not meant to be called directly, but subclassed.
 *
 * @package AF\Controller
 */
abstract class AFController extends Controller
{
    protected $currentModule = null;

    /**
     * Array of currently enabled modules
     * @return array
     */
    abstract function getAllowedModules();

    protected function setCurrentModule($moduleName)
    {
        if (in_array($moduleName, $this->getAllowedModules())) {
            $this->currentModule = $moduleName;
        } else {
            throw new AccessException("Trying to access unconfigured module: $moduleName");
        }
    }

    public function render($view, array $parameters = array(), Response $response = null)
    {
        // This is used in the navbar
        $parameters["module"] = $this->currentModule;

        return parent::render($view, $parameters, $response);
    }


}