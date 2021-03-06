package by.myself.controller;

import by.myself.model.ProductModel;
import by.myself.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class CatalogController {

    private List<ProductModel> viewProducts = new ArrayList<>();
    private final ProductService productService;
    private PagedListHolder<ProductModel> pagedListHolder = new PagedListHolder<>(viewProducts);

    @Autowired
    public CatalogController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/catalog")
    public String showSearchPage(@RequestParam("page") Optional<String> page) {
        if (page.isPresent() && !page.get().matches("/D+")) {
            Integer requiredPage = Integer.valueOf(page.get());
            if (requiredPage <= pagedListHolder.getPageCount() && requiredPage > 0) {
                pagedListHolder.setPage(requiredPage - 1);
            }
        }
        return "catalog";
    }

    @ModelAttribute("products")
    public List<ProductModel> products() {
        return viewProducts;
    }

    @ModelAttribute("pagedListHolder")
    public PagedListHolder<ProductModel> pagedListHolder() {
        return pagedListHolder;
    }

    @PostMapping("/catalog")
    public String showRequiredProducts(String query, Double cost, String pagination, Integer limit) {
        if (query.matches(" +") && cost == 0d) {
            return "redirect:/catalog";
        }
        if (!viewProducts.isEmpty()) {
            viewProducts.clear();
        }
        viewProducts = productService.searchProducts(query, cost, limit);
        pagedListHolder.setSource(viewProducts);
        pagedListHolder.setPageSize(Integer.valueOf(pagination));
        return "redirect:/catalog";
    }
}
