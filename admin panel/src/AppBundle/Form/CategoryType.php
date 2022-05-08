<?php
namespace AppBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;

class CategoryType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
       $builder
            ->add('title',null,array("label"=>"Category"))
            ->add("description")
        ;
        $builder->add('save', 'submit',array("label"=>"SAVE THE CATEGORY"));

    }
    public function getName()
    {
        return 'Category';
    }
}
?>
